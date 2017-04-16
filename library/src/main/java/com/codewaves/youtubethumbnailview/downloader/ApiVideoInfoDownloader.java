package com.codewaves.youtubethumbnailview.downloader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codewaves.youtubethumbnailview.Utils;
import com.codewaves.youtubethumbnailview.VideoInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ApiVideoInfoDownloader implements VideoInfoDownloader {
   private String apiKey;
   private OkHttpClient client;

   public ApiVideoInfoDownloader(@Nullable String apiKey) {
      this.apiKey = apiKey;
      this.client = new OkHttpClient();
   }

   @Override
   @NonNull
   public VideoInfo download(@NonNull String url, int minThumbnailWidth) throws IOException {
      final String id = Utils.getVideoIdFromUrl(url);
      final String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet" +
            "&fields=items(snippet(title,thumbnails)),items(contentDetails(duration))" +
            "&key=" + apiKey +
            "&id=" + id;

      final Request request = new Request.Builder()
            .url(apiUrl)
            .build();

      final Response response = client.newCall(request).execute();
      try {
         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
         }

         final JsonElement root = new JsonParser().parse(response.body().charStream());
         final JsonArray items = root.getAsJsonObject().get("items").getAsJsonArray();
         if (items.size() <= 0) {
            throw new IOException("Cannot find video");
         }

         final JsonObject snippet = items.get(0).getAsJsonObject().get("snippet").getAsJsonObject();
         final String title = snippet.get("title").getAsString();
         final String thumbnail = findThumbnailUrl(snippet, minThumbnailWidth);

         final JsonObject contentDetails = items.get(0).getAsJsonObject().get("contentDetails").getAsJsonObject();
         final String duration = contentDetails.get("duration").getAsString();
         final int seconds = Utils.durationToSeconds(duration);

         return new VideoInfo(title, thumbnail, seconds);

      }
      finally {
         response.close();
      }
   }

   private String findThumbnailUrl(@NonNull JsonObject snippet, int minWidth) {
      String thumbnailUrl = null;
      int thumbnailWidth = Integer.MAX_VALUE;
      try {
         final JsonObject thumbnails = snippet.get("thumbnails").getAsJsonObject();
         for (final Map.Entry<String, JsonElement> entry : thumbnails.entrySet()) {
            final JsonObject thumbnail = entry.getValue().getAsJsonObject();
            final int width = thumbnail.get("width").getAsInt();

            if (thumbnailUrl == null ||
                  (width >= minWidth && thumbnailWidth < minWidth) ||
                  (width >= minWidth && width < thumbnailWidth) ||
                  (width < minWidth && width > thumbnailWidth)) {
               thumbnailUrl = thumbnail.get("url").getAsString();
               thumbnailWidth = width;
            }
         }
      }
      catch (Exception e) {
         // Ignore, we can't find any thumbnail
      }

      return thumbnailUrl;
   }
}
