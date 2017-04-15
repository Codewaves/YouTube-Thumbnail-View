package com.codewaves.youtubethumbnailview;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.downloader.OembedVideoInfoDownloader;
import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ThumbnailLoader {
   private static final int DEFAULT_THREAD_POOL_SIZE = 3;

   private Executor executor;
   private VideoInfoDownloader infoDownloader;

   private volatile static ThumbnailLoader instance;

   private static ThumbnailLoader getInstance() {
      if (instance == null) {
         synchronized (ThumbnailLoader.class) {
            if (instance == null) {
               instance = new ThumbnailLoader();
            }
         }
      }
      return instance;
   }

   private ThumbnailLoader() {
      final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
      executor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, taskQueue);

      infoDownloader = new OembedVideoInfoDownloader();
   }

   private void fetchVideoInfo(@NonNull String url, @NonNull VideoInfoDownloadListener listener, @NonNull Handler handler) {
      executor.execute(new VideoInfoTask(url, infoDownloader, listener, handler));
   }

   static void fetchVideoInfo(@NonNull String url, @NonNull VideoInfoDownloadListener listener) {
      final Handler handler = new Handler();
      getInstance().fetchVideoInfo(url, listener, handler);
   }
}
