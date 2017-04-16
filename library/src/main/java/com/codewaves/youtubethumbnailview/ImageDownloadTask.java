package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.codewaves.youtubethumbnailview.listener.ImageDownloadListener;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ImageDownloadTask extends CancellableTask {
   private final OkHttpClient client;
   private final String url;
   private final WeakReference<ImageView> viewRef;
   private final Handler handler;

   ImageDownloadTask(@NonNull OkHttpClient client, @NonNull String url, @NonNull WeakReference<ImageView> viewRef, @NonNull Handler handler) {
      this.client = client;
      this.url = url;
      this.viewRef = viewRef;
      this.handler = handler;
   }

   @Override
   public void run() {
      try {
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
                  setBitmap(bitmap);
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

   @Override
   void onCancel() {
   }

   private void postFailure(@NonNull final Throwable failCause) {
      postTask(new Runnable() {
         @Override
         public void run() {
            setBitmap(null);
         }
      });
   }

   private void postTask(@NonNull Runnable task) {
      handler.post(task);
   }

   private void setBitmap(@Nullable Bitmap bitmap) {
      if (!isCanceled()) {
         // Set image
         final ImageView view = viewRef.get();
         if (view != null) {
            view.setImageBitmap(bitmap);
         }
      }
   }
}
