package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.listener.ImageDownloadListener;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ImageDownloadTask implements Runnable {
   private final String url;
   private final ImageDownloadListener listener;
   private final Handler handler;

   ImageDownloadTask(@NonNull String url, @NonNull ImageDownloadListener listener, @NonNull Handler handler) {
      this.url = url;
      this.listener = listener;
      this.handler = handler;
   }

   @Override
   public void run() {
      try {

         final OkHttpClient client = new OkHttpClient();
         final Request request = new Request.Builder()
               .url(url)
               .build();

         final Response response = client.newCall(request).execute();
         try {
            final InputStream inputStream = response.body().byteStream();
            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            postTask(new Runnable() {
               @Override
               public void run() {
                  listener.onDownloadFinished(bitmap);
               }
            });
         }
         finally {
            response.close();
         }
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
