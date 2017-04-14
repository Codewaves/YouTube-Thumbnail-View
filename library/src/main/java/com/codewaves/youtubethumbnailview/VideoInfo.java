package com.codewaves.youtubethumbnailview;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class VideoInfo {
   private String title;
   private String thumbnailUrl;
   private int length;

   public VideoInfo(String title, String thumbnailUrl, int length) {
      this.title = title;
      this.thumbnailUrl = thumbnailUrl;
      this.length = length;
   }

   public String getTitle() {
      return title;
   }

   public String getThumbnailUrl() {
      return thumbnailUrl;
   }

   public int getLength() {
      return length;
   }
}
