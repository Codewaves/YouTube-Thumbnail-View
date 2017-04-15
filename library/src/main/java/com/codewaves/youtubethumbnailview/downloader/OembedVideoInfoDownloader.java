package com.codewaves.youtubethumbnailview.downloader;

import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.VideoInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class OembedVideoInfoDownloader implements VideoInfoDownloader {
   @Override
   @NonNull
   public VideoInfo download(@NonNull String url) throws IOException {
      String encodedUrl = URLEncoder.encode(url);
      final String ombedUrl = "http://www.youtube.com/oembed?url=" + encodedUrl + "&format=json";

      final OkHttpClient client = new OkHttpClient();
      final Request request = new Request.Builder()
            .url(ombedUrl)
            .build();

      final Response response = client.newCall(request).execute();
      try {
         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
         }


         final Gson gson = new Gson();
         final OembedResponse result = gson.fromJson(response.body().charStream(), OembedResponse.class);

         if (result.title == null && result.thumbnail_url == null) {
            throw new IOException("Invalid youtube oembed response.");
         }

         return new VideoInfo(result.title, result.thumbnail_url, 0);
      }
      finally {
         response.close();
      }
   }

   private static class OembedResponse {
      String title;
      String thumbnail_url;
   }
}
