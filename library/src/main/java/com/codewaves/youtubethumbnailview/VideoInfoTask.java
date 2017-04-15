package com.codewaves.youtubethumbnailview;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

import java.io.IOException;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class VideoInfoTask implements Runnable {
   private final String url;
   private final VideoInfoDownloader downloader;
   private final VideoInfoDownloadListener listener;
   private final Handler handler;

   VideoInfoTask(@NonNull String url, @NonNull VideoInfoDownloader downloader, @NonNull VideoInfoDownloadListener listener, @NonNull Handler handler) {
      this.url = url;
      this.downloader = downloader;
      this.listener = listener;
      this.handler = handler;
   }

   @Override
   public void run() {
      try {
         final VideoInfo info = downloader.download(url);
         postTask(new Runnable() {
            @Override
            public void run() {
               listener.onDownloadFinished(info);
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
}
