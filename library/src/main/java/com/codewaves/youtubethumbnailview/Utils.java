package com.codewaves.youtubethumbnailview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergej Kravcenko on 4/16/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class Utils {
   private static final int HOUR_SECONDS = 3600;
   private static final int MINUTE_SECONDS = 60;

   private static final String TIME_DELIMITER = ":";
   private static final String TIME_PADDING = "0";

   private static final String REGEXP_ID_PATTERN = "(?i)https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube(?:-nocookie)?\\.com" +
         "\\S*?[^\\w\\s-])([\\w-]{11})(?=[^\\w-]|$)(?![?=&+%\\w.-]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w.-]*";

   static public int durationToSeconds(@Nullable String duration) {
      if (duration == null || duration.length() <= 0) {
         return 0;
      }

      int current = 0;
      int seconds = 0;
      for (final char c : duration.toCharArray()) {
         if (Character.isDigit(c)) {
            current = current * 10 + Character.getNumericValue(c);
         }
         else if (c == 'H') {
            seconds = seconds + current * HOUR_SECONDS;
            current = 0;
         }
         else if (c == 'M') {
            seconds = seconds + current * MINUTE_SECONDS;
            current = 0;
         }
         else if (c == 'S') {
            seconds = seconds + current;
            current = 0;
         }
      }

      return seconds;
   }

   static public String getVideoIdFromUrl(@NonNull String url) {
      final Pattern pattern = Pattern.compile(REGEXP_ID_PATTERN);
      final Matcher matcher = pattern.matcher(url);
      if (matcher.matches()) {
         return matcher.group(1);
      }

      throw new IllegalArgumentException("Cannot extract video id from url");
   }

   @NonNull
   static public String secondsToTime(int totalSeconds) {
      final int hours = totalSeconds / HOUR_SECONDS;
      final int minutes = (totalSeconds - hours * HOUR_SECONDS) / MINUTE_SECONDS;
      final int seconds = totalSeconds - hours * HOUR_SECONDS - minutes * MINUTE_SECONDS;

      String timeString = "";
      if (hours > 0) {
         timeString += String.valueOf(hours) + TIME_DELIMITER;
      }

      if (minutes < 10) {
         timeString += TIME_PADDING + String.valueOf(minutes) + TIME_DELIMITER;
      }
      else {
         timeString += String.valueOf(minutes) + TIME_DELIMITER;
      }

      if (seconds < 10) {
         timeString += TIME_PADDING + String.valueOf(seconds);
      }
      else {
         timeString += String.valueOf(seconds);
      }

      return timeString;
   }
}
