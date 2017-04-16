package com.codewaves.youtubethumbnailview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codewaves.youtubethumbnailview.downloader.ApiVideoInfoDownloader;
import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;
import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;

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

   private ThreadPoolExecutor executor;
   private VideoInfoDownloader defaultInfoDownloader;
   private ImageLoader defaultImageLoader;

   private WeakHashMap<ThumbnailView, ThumbnailTask> taskMap;

   private volatile static ThumbnailLoader instance;

   private static ThumbnailLoader initInstance(@NonNull Context context) {
      if (instance == null) {
         synchronized (ThumbnailLoader.class) {
            if (instance == null) {
               instance = new ThumbnailLoader(context);
            }
         }
      }
      return instance;
   }

   public static ThumbnailLoader initialize(@NonNull Context context) {
      return initInstance(context);
   }

   public ThumbnailLoader setVideoInfoDownloader(@NonNull VideoInfoDownloader defaultInfoDownloader) {
      this.defaultInfoDownloader = defaultInfoDownloader;
      return this;
   }

   public ThumbnailLoader setImageLoader(@NonNull ImageLoader defaultImageLoader) {
      this.defaultImageLoader = defaultImageLoader;
      return this;
   }

   private ThumbnailLoader(@NonNull Context context) {
      String googleApiKey = null;
      try {
         final ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
         if (appInfo.metaData != null) {
            googleApiKey = appInfo.metaData.getString("com.codewaves.youtubethumbnailview.ApiKey");
         }
      } catch (PackageManager.NameNotFoundException e) {
         // Ignore
      }

      final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
      executor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, taskQueue);

      taskMap = new WeakHashMap<>();
      defaultInfoDownloader = new ApiVideoInfoDownloader(googleApiKey);
      defaultImageLoader = new SimpleImageLoader(executor);
   }

   private void loadThumbnailInt(@NonNull ThumbnailView view,
                                 @NonNull String url,
                                 int minThumbnailSize,
                                 @Nullable ThumbnailLoadingListener listener,
                                 @Nullable ImageLoader imageLoader) {
      final ThumbnailTask existingTask = taskMap.get(view);
      if (existingTask != null) {
         existingTask.cancel();
         taskMap.remove(view);
      }

      if (imageLoader == null) {
         imageLoader = defaultImageLoader;
      }

      final ThumbnailTask task = new ThumbnailTask(executor, view, url, minThumbnailSize, defaultInfoDownloader, listener, imageLoader);
      taskMap.put(view, task);
      task.run();
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
      final ThumbnailTask existingTask = taskMap.get(view);
      if (existingTask != null) {
         existingTask.cancel();
         taskMap.remove(view);
      }
   }

   static void cancelThumbnailLoad(@NonNull ThumbnailView view) {
      instance.cancelThumbnailLoadInt(view);
   }
}
