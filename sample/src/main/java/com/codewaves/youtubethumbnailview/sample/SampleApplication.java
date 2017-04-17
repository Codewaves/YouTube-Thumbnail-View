package com.codewaves.youtubethumbnailview.sample;

import android.app.Application;

import com.codewaves.youtubethumbnailview.ThumbnailLoader;

/**
 * Created by Sergej Kravcenko on 4/16/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class SampleApplication extends Application {
   @Override
   public void onCreate() {
      super.onCreate();

      ThumbnailLoader.initialize(getApplicationContext());
   }
}
