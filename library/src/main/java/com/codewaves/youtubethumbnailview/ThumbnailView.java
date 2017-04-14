package com.codewaves.youtubethumbnailview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailView extends RelativeLayout {
   private String videoId;

   public ThumbnailView(Context context) {
      this(context, null);
   }

   public ThumbnailView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public ThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public ThumbnailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(context, attrs);
   }

   private void init(Context context, AttributeSet attrs) {
      // Add thumbnail image
      final ImageView thumbnail = new ImageView(context);
      thumbnail.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
      addView(thumbnail);
   }

   public void displayThumbnail(@NonNull String url) {
      ThumbnailLoader.fetchVideoInfo(url, new VideoInfoDownloadListener() {
         @Override
         public void onDownloadFinished(@NonNull VideoInfo info) {
         }

         @Override
         public void onDownloadFailed(@NonNull Throwable error) {
         }
      });
   }
}
