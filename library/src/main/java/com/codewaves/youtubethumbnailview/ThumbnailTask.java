package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;

import java.io.IOException;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ThumbnailTask implements Runnable {
   private final String url;
   private final int minThumbnailWidth;
   private final VideoInfoDownloader infoDownloader;
   private final ImageLoader imageLoader;
   private final ThumbnailDownloadListener listener;
   private final Handler handler;
   private final boolean ignoreCache;

   ThumbnailTask(@NonNull String url,
                 int minThumbnailWidth,
                 @NonNull VideoInfoDownloader infoDownloader,
                 @NonNull ImageLoader imageLoader,
                 @NonNull ThumbnailDownloadListener listener,
                 @NonNull Handler handler,
                 boolean ignoreCache) {
      this.url = url;
      this.minThumbnailWidth = minThumbnailWidth;
      this.infoDownloader = infoDownloader;
      this.imageLoader = imageLoader;
      this.listener = listener;
      this.handler = handler;
      this.ignoreCache = ignoreCache;
   }

   @Override
   public void run() {
      try {
         // Check cache first
         final VideoInfo info = fetchInfo();
         final Bitmap bitmap = imageLoader.load(info.getThumbnailUrl());
         postTask(new Runnable() {
            @Override
            public void run() {
               listener.onDownloadFinished(info, bitmap);
            }
         });
      }
      catch (IOException e) {
         postFailure(e);
      }
      catch (Exception e) {
         postFailure(e);
      }
   }

   private void postFailure(@NonNull final Throwable failCause) {
      postTask(new Runnable() {
         @Override
         public void run() {
            listener.onDownloadFailed(failCause);
         }
      });
   }

   private void postTask(@NonNull Runnable task) {
      handler.post(task);
   }

   private VideoInfo fetchInfo() throws Exception {
      VideoInfo info = null;
      if (!ignoreCache) {
         info = ThumbnailLoader.findInfoInCache(url);
      }
      if (info == null) {
         info = infoDownloader.download(url, minThumbnailWidth);
         ThumbnailLoader.putInfoIntoCache(url, info);
      }
      return info;
   }
}
