package com.codewaves.youtubethumbnailview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codewaves.youtubethumbnailview.listener.SimpleThumbnailLoadingListener;
import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailView extends RelativeLayout {
   private static final int DEFAULT_TITLE_MAX_LINES = 1;
   private static final int DEFAULT_MIN_THUMBNAIL_SIZE = 320;

   private ImageView thumbnail;
   private TextView title;

   private int minThumbnailSize;

   private int dpToPx(Context context, float dp) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return Math.round(dp * scale);
   }

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
      // Attributes
      final TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ThumbnailView, 0, 0);

      this.minThumbnailSize = attr.getInteger(R.styleable.ThumbnailView_youtube_minThumbnailWidth, DEFAULT_MIN_THUMBNAIL_SIZE);
      final int titleColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleColor, Color.WHITE);
      final int titleBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleBackgroundColor, 0x80000000);
      final int titlePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingLeft, dpToPx(context, 10.0f));
      final int titlePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingRight, dpToPx(context, 10.0f));
      final int titlePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingTop, dpToPx(context, 5.0f));
      final int titlePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingBottom, dpToPx(context, 5.0f));
      final float titleTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_titleTextSize, getResources().getDimension(R.dimen.title_text_size));
      final int titleMaxLines = attr.getInteger(R.styleable.ThumbnailView_youtube_titleMaxLines, DEFAULT_TITLE_MAX_LINES);

      attr.recycle();


      // Add thumbnail image
      thumbnail = new ImageView(context);
      thumbnail.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);

      addView(thumbnail);

      // Add video title
      title = new TextView(context);
      title.setTextColor(titleColor);
      title.setBackgroundColor(titleBackgroundColor);
      title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
      title.setMaxLines(titleMaxLines);
      title.setEllipsize(TextUtils.TruncateAt.END);
      title.setPadding(titlePaddingLeft, titlePaddingTop, titlePaddingRight, titlePaddingBottom);
      title.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

      addView(title);
   }

   public void displayThumbnail(@NonNull String url) {
      displayThumbnail(url, new SimpleThumbnailLoadingListener(), null);
   }

   public void displayThumbnail(@NonNull String url, @NonNull ThumbnailLoadingListener listener) {
      displayThumbnail(url, listener, null);
   }

   public void displayThumbnail(final @NonNull String url, final @NonNull ThumbnailLoadingListener listener, final @Nullable ImageLoader imageLoader) {
      listener.onLoadingStarted(url, this);

      ThumbnailLoader.fetchVideoInfo(url, minThumbnailSize, new VideoInfoDownloadListener() {
         @Override
         public void onDownloadFinished(@NonNull VideoInfo info) {
            // Update views and start thumbnail download
            title.setText(info.getTitle());
            loadThumbnailImage(info.getThumbnailUrl(), imageLoader);
            listener.onLoadingComplete(url, ThumbnailView.this);
         }

         @Override
         public void onDownloadFailed(@NonNull Throwable error) {
            listener.onLoadingFailed(url, ThumbnailView.this, error);
         }
      });
   }

   private void loadThumbnailImage(@NonNull String imageUrl, @Nullable ImageLoader imageLoader) {
      if (imageLoader != null) {
         imageLoader.load(imageUrl, thumbnail);
      }
      else {
         ThumbnailLoader.fetchThumbnail(imageUrl, thumbnail);
      }
   }
}
