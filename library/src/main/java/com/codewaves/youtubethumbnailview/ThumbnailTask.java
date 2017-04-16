package com.codewaves.youtubethumbnailview;


import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;
import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Sergej Kravcenko on 4/16/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ThumbnailTask extends CancellableTask {
   private final ThreadPoolExecutor executor;
   private final ThumbnailView view;
   private final String url;
   private final int minThumbnailSize;
   private final VideoInfoDownloader infoDownloader;
   private final ThumbnailLoadingListener listener;
   private final ImageLoader imageLoader;

   private VideoInfoTask infoTask;

   ThumbnailTask(@NonNull ThreadPoolExecutor executor,
                 @NonNull ThumbnailView view,
                 @NonNull String url,
                 int minThumbnailSize,
                 @NonNull VideoInfoDownloader infoDownloader,
                 @Nullable ThumbnailLoadingListener listener,
                 @NonNull ImageLoader imageLoader) {
      this.executor = executor;
      this.view = view;
      this.url = url;
      this.minThumbnailSize = minThumbnailSize;
      this.infoDownloader = infoDownloader;
      this.listener = listener;
      this.imageLoader = imageLoader;
   }

   @Override
   public void run() {
      if (listener != null) {
         listener.onLoadingStarted(url, view);
      }

      final Handler handler = new Handler();
      infoTask = new VideoInfoTask(url, minThumbnailSize, infoDownloader, new VideoInfoDownloadListener() {
         @Override
         public void onDownloadFinished(@NonNull VideoInfo info) {
            if (!isCanceled()) {
               // Update views and start thumbnailView download
               view.setThumbnailInfo(info.getTitle(), info.getLength());
               imageLoader.load(info.getThumbnailUrl(), view.getThumbnailView());

               finish();
               if (listener != null) {
                  listener.onLoadingComplete(url, view);
               }
            }
         }

         @Override
         public void onDownloadFailed(@NonNull Throwable error) {
            finish();
            if (listener != null) {
               listener.onLoadingFailed(url, view, error);
            }
         }
      }, handler);
      executor.execute(infoTask);
   }

   @Override
   public void onCancel() {
      if (infoTask != null) {
         executor.remove(infoTask);
      }
      imageLoader.cancel(view.getThumbnailView());

      if (listener != null) {
         listener.onLoadingCanceled(url, view);
      }
   }
}
