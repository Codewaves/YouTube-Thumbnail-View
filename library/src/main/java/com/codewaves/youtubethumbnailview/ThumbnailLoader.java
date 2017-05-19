package com.codewaves.youtubethumbnailview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.codewaves.youtubethumbnailview.downloader.ApiVideoInfoDownloader;
import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;

import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailLoader {
   private static final int DEFAULT_THREAD_POOL_SIZE = 3;
   private static final int DEFAULT_INFO_CACHE_SIZE = 64;

   private ThreadPoolExecutor executor;
   private VideoInfoDownloader defaultInfoDownloader;
   private ImageLoader defaultImageLoader;
   private boolean isCacheEnabled = true;
   private LruCache<String, VideoInfo> infoCache = new LruCache<>(DEFAULT_INFO_CACHE_SIZE);

   private WeakHashMap<ThumbnailView, ThumbnailRequest> requestMap;

   private volatile static ThumbnailLoader instance;

   private static ThumbnailLoader initInstance(@Nullable Context context, @Nullable String googleApiKey) {
      if (instance == null) {
         synchronized (ThumbnailLoader.class) {
            if (instance == null) {
               instance = new ThumbnailLoader(context, googleApiKey);
            }
         }
      }
      return instance;
   }

   public static ThumbnailLoader initialize(@NonNull Context context) {
      return initInstance(context, null);
   }

   public static ThumbnailLoader initialize(@NonNull String googleApiKey) {
      return initInstance(null, googleApiKey);
   }

   public static ThumbnailLoader initialize() {
      return initInstance(null, null);
   }

   public ThumbnailLoader setVideoInfoDownloader(@NonNull VideoInfoDownloader defaultInfoDownloader) {
      this.defaultInfoDownloader = defaultInfoDownloader;
      return this;
   }

   public ThumbnailLoader setImageLoader(@NonNull ImageLoader defaultImageLoader) {
      this.defaultImageLoader = defaultImageLoader;
      return this;
   }

   public ThumbnailLoader enableInfoCache(boolean enable) {
      this.isCacheEnabled = enable;
      return this;
   }

   private ThumbnailLoader(@Nullable Context context, @Nullable String googleApiKey) {
      String metaGoogleApiKey = googleApiKey;
      if (context != null) {
         try {
            final ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
               metaGoogleApiKey = appInfo.metaData.getString("com.codewaves.youtubethumbnailview.ApiKey");
            }
         }
         catch (PackageManager.NameNotFoundException e) {
            // Ignore
         }
      }

      final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
      executor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, taskQueue);

      requestMap = new WeakHashMap<>();
      defaultInfoDownloader = new ApiVideoInfoDownloader(metaGoogleApiKey);
      defaultImageLoader = new SimpleImageLoader();
   }

   private void loadThumbnailInt(@NonNull ThumbnailView view,
                                 @NonNull String url,
                                 int minThumbnailSize,
                                 @Nullable ThumbnailLoadingListener listener,
                                 @Nullable ImageLoader imageLoader) {
      final ThumbnailRequest existingRequest = requestMap.get(view);
      if (existingRequest != null) {
         existingRequest.cancel();
         requestMap.remove(view);
      }

      final ThumbnailRequest request = new ThumbnailRequest(executor,
            view, url, minThumbnailSize,
            defaultInfoDownloader, imageLoader == null ? defaultImageLoader : imageLoader,
            listener,
            !isCacheEnabled);
      requestMap.put(view, request);
      request.run();
   }

   static void loadThumbnail(@NonNull ThumbnailView view,
                             @NonNull String url,
                             int minThumbnailSize,
                             @Nullable ThumbnailLoadingListener listener,
                             @Nullable ImageLoader imageLoader) {
      if (instance == null) {
         throw new IllegalStateException("Youtube thumbnail library is not initialized");
      }

      instance.loadThumbnailInt(view, url, minThumbnailSize, listener, imageLoader);
   }

   private void cancelThumbnailLoadInt(@NonNull ThumbnailView view) {
      final ThumbnailRequest existingRequest = requestMap.get(view);
      if (existingRequest != null) {
         existingRequest.cancel();
         requestMap.remove(view);
      }
   }

   static void cancelThumbnailLoad(@NonNull ThumbnailView view) {
      instance.cancelThumbnailLoadInt(view);
   }

   static VideoInfo findInfoInCache(@NonNull String url) {
      if (instance == null) {
         throw new IllegalStateException("Youtube thumbnail library is not initialized");
      }
      return instance.infoCache.get(url);
   }

   static void putInfoIntoCache(@NonNull String url, @NonNull VideoInfo info) {
      if (instance == null) {
         throw new IllegalStateException("Youtube thumbnail library is not initialized");
      }
      instance.infoCache.put(url, info);
   }
}
