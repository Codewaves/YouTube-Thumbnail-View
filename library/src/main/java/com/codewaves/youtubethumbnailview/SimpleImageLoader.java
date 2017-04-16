package com.codewaves.youtubethumbnailview;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.ImageView;


import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.OkHttpClient;

/**
 * Created by Sergej Kravcenko on 4/17/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class SimpleImageLoader implements ImageLoader {
   private OkHttpClient client = new OkHttpClient();
   private ThreadPoolExecutor executor;
   private WeakHashMap<ImageView, ImageDownloadTask> taskMap;

   SimpleImageLoader(@NonNull ThreadPoolExecutor executor) {
      this.executor = executor;
      this.taskMap = new WeakHashMap<>();
   }

   @Override
   public void load(String url, ImageView imageView) {
      if (url == null || imageView == null) {
         return;
      }

      final Handler handler = new Handler();
      final WeakReference<ImageView> viewRef = new WeakReference<>(imageView);
      final ImageDownloadTask task = new ImageDownloadTask(client, url, viewRef, handler);

      taskMap.put(imageView, task);
      executor.execute(task);
   }

   @Override
   public void cancel(ImageView imageView) {
      if (imageView == null) {
         return;
      }

      final ImageDownloadTask existingTask = taskMap.get(imageView);
      if (existingTask != null) {
         existingTask.cancel();
         taskMap.remove(imageView);
      }
   }
}
