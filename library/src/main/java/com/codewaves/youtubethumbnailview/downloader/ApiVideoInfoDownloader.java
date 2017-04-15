package com.codewaves.youtubethumbnailview.downloader;

import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.VideoInfo;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ApiVideoInfoDownloader implements VideoInfoDownloader {
   private static final String REGEXP_ID_PATTERN = "^(?:(?:https?:\\/\\/)?(?:www\\.)?)?(youtube(?:-nocookie)?\\.com|youtu\\.be)\\/.*?(?:embed|e|v|watch\\?.*?v=)?\\/?([a-z0-9]+)";

   @Override
   @NonNull
   public VideoInfo download(@NonNull String url) throws IOException {
      final String id = getVideoIdFromUrl(url);

      return new VideoInfo("", "", 0);
   }

   private String getVideoIdFromUrl(String url) {
      final Pattern pattern = Pattern.compile(REGEXP_ID_PATTERN);
      final Matcher matcher = pattern.matcher(url);
      if (matcher.matches()) {
         return matcher.group(2);
      }

      throw new IllegalArgumentException("Cannot extract video id from url");
   }
}
