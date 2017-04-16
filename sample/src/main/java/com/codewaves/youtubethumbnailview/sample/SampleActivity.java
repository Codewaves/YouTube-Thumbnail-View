package com.codewaves.youtubethumbnailview.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.codewaves.youtubethumbnailview.ThumbnailView;
import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class SampleActivity extends AppCompatActivity {
   private static final String TAG = "SampleActivity";
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sample);

      final ThumbnailView thumb = (ThumbnailView)findViewById(R.id.thumbnail);
      thumb.fetchThumbnail("https://www.youtube.com/watch?v=iCkYw3cRwLo", new ThumbnailLoadingListener() {
         @Override
         public void onLoadingStarted(@NonNull String url, @NonNull View view) {
            Log.i(TAG, "Thumbnail load started.");
         }

         @Override
         public void onLoadingComplete(@NonNull String url, @NonNull View view) {
            Log.i(TAG, "Thumbnail load finished.");
         }

         @Override
         public void onLoadingFailed(@NonNull String url, @NonNull View view, Throwable error) {
            Log.e(TAG, "Thumbnail load failed. " + error.getMessage());
         }
      });
   }
}
