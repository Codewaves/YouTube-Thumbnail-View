package com.codewaves.youtubethumbnailview.listener;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class SimpleThumbnailLoadingListener implements ThumbnailLoadingListener {
   @Override
   public void onLoadingStarted(@NonNull String url, @NonNull View view) {
      // Empty implementation
   }

   @Override
   public void onLoadingComplete(@NonNull String url, @NonNull View view) {
      // Empty implementation
   }

   @Override
   public void onLoadingFailed(@NonNull String url, @NonNull View view, Throwable error) {
      // Empty implementation
   }
}
