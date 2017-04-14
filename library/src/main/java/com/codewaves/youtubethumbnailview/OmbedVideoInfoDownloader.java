package com.codewaves.youtubethumbnailview;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class OmbedVideoInfoDownloader implements VideoInfoDownloader {
   @Override
   @NonNull
   public VideoInfo download(@NonNull String url) throws IOException {
      return new VideoInfo("", "", 0);
   }
}
