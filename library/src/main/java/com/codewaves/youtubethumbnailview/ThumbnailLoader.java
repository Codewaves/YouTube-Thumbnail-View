package com.codewaves.youtubethumbnailview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.codewaves.youtubethumbnailview.downloader.ApiVideoInfoDownloader;
import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;
import com.codewaves.youtubethumbnailview.listener.ImageDownloadListener;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailLoader {
   private static final int DEFAULT_THREAD_POOL_SIZE = 3;

   private Executor executor;
   private VideoInfoDownloader defaultInfoDownloader;
   private ImageLoader defaultImageLoader;
   private String googleApiKey = "";

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
      try {
         ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
         if (appInfo.metaData != null) {
            googleApiKey = appInfo.metaData.getString("com.codewaves.youtubethumbnailview.ApiKey");
         }
      } catch (PackageManager.NameNotFoundException e) {
         // Ignore
      }

      final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
      executor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, taskQueue);

      defaultInfoDownloader = new ApiVideoInfoDownloader(googleApiKey);

      defaultImageLoader = new ImageLoader() {
         @Override
         public void load(String url, ImageView imageView) {
            if (url == null || imageView == null) {
               return;
            }

            final Handler handler = new Handler();
            final WeakReference<ImageView> viewRef = new WeakReference<>(imageView);
            executor.execute(new ImageDownloadTask(url, new ImageDownloadListener() {
               @Override
               public void onDownloadFinished(@NonNull Bitmap image) {
                  // Set image
                  final ImageView view = viewRef.get();
                  if (view != null) {
                     view.setImageBitmap(image);
                  }
               }

               @Override
               public void onDownloadFailed(@NonNull Throwable error) {
                  // Set default image
                  final ImageView view = viewRef.get();
                  if (view != null) {
                     view.setImageBitmap(null);
                  }
               }
            }, handler));
         }
      };
   }

   private void fetchVideoInfo(@NonNull String url, int minThumbnailWidth, @NonNull VideoInfoDownloadListener listener, @NonNull Handler handler) {
      executor.execute(new VideoInfoTask(url, minThumbnailWidth, defaultInfoDownloader, listener, handler));
   }

   static void fetchVideoInfo(@NonNull String url, int minThumbnailWidth, @NonNull VideoInfoDownloadListener listener) {
      if (instance == null) {
         throw new RuntimeException("Youtube thumbnail library is not initialized");
      }

      final Handler handler = new Handler();
      instance.fetchVideoInfo(url, minThumbnailWidth, listener, handler);
   }

   private void fetchThumbnailInternal(@NonNull String url, @NonNull ImageView imageView) {
      defaultImageLoader.load(url, imageView);
   }

   static void fetchThumbnail(@NonNull String url, @NonNull ImageView imageView) {
      if (instance == null) {
         throw new RuntimeException("Youtube thumbnail library is not initialized");
      }

      instance.fetchThumbnailInternal(url, imageView);
   }
}
