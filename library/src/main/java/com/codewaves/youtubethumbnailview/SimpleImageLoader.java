package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sergej Kravcenko on 4/17/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class SimpleImageLoader implements ImageLoader {
   private OkHttpClient client = new OkHttpClient();

   @Override
   @Nullable
   public Bitmap load(String url) throws IOException {
      if (url == null) {
         return null;
      }

      final Request request = new Request.Builder()
            .url(url)
            .build();

      final Response response = client.newCall(request).execute();
      try {
         final InputStream inputStream = response.body().byteStream();
         return BitmapFactory.decodeStream(inputStream);
      }
      finally {
         response.close();
      }
   }
}
