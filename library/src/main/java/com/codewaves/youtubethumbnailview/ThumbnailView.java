package com.codewaves.youtubethumbnailview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;
import com.codewaves.youtubethumbnailview.listener.VideoInfoDownloadListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class ThumbnailView extends RelativeLayout {
   private static final int DEFAULT_TITLE_MAX_LINES = 1;
   private static final int DEFAULT_MIN_THUMBNAIL_SIZE = 320;

   private ImageView thumbnailView;
   private TextView titleView;
   private TextView timeView;

   private boolean isLoaded;
   private int minThumbnailSize;
   private boolean titleVisible;
   private boolean timeVisible;

   private int dpToPx(Context context, float dp) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return Math.round(dp * scale);
   }

   @IntDef({VISIBLE, INVISIBLE, GONE})
   @Retention(RetentionPolicy.SOURCE)
   @interface Visibility {}

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

      minThumbnailSize = attr.getInteger(R.styleable.ThumbnailView_youtube_minThumbnailWidth, DEFAULT_MIN_THUMBNAIL_SIZE);

      titleVisible = attr.getBoolean(R.styleable.ThumbnailView_youtube_titleVisible, true);
      timeVisible = attr.getBoolean(R.styleable.ThumbnailView_youtube_timeVisible, true);

      final int titleColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleColor, Color.WHITE);
      final int titleBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_titleBackgroundColor, 0x80000000);
      final int titlePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingLeft, dpToPx(context, 10.0f));
      final int titlePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingRight, dpToPx(context, 10.0f));
      final int titlePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingTop, dpToPx(context, 5.0f));
      final int titlePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_titlePaddingBottom, dpToPx(context, 5.0f));
      final float titleTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_titleTextSize, getResources().getDimension(R.dimen.title_text_size));
      final int titleMaxLines = attr.getInteger(R.styleable.ThumbnailView_youtube_titleMaxLines, DEFAULT_TITLE_MAX_LINES);

      final int timeColor = attr.getColor(R.styleable.ThumbnailView_youtube_timeColor, Color.WHITE);
      final int timeBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_timeBackgroundColor, 0x80000000);
      final int timePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingLeft, dpToPx(context, 5.0f));
      final int timePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingRight, dpToPx(context, 5.0f));
      final int timePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingTop, dpToPx(context, 0.0f));
      final int timePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingBottom, dpToPx(context, 0.0f));
      final int timeMarginBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginBottom, dpToPx(context, 10.0f));
      final int timeMarginRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginRight, dpToPx(context, 10.0f));
      final float timeTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_timeTextSize, getResources().getDimension(R.dimen.time_text_size));

      attr.recycle();


      // Add thumbnailView image
      thumbnailView = new ImageView(context);
      thumbnailView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);

      addView(thumbnailView);

      // Add video titleView
      titleView = new TextView(context);
      titleView.setTextColor(titleColor);
      titleView.setBackgroundColor(titleBackgroundColor);
      titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
      titleView.setMaxLines(titleMaxLines);
      titleView.setEllipsize(TextUtils.TruncateAt.END);
      titleView.setPadding(titlePaddingLeft, titlePaddingTop, titlePaddingRight, titlePaddingBottom);
      titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      titleView.setVisibility(GONE);

      addView(titleView);

      // Add video length
      timeView = new TextView(context);
      timeView.setTextColor(timeColor);
      timeView.setBackgroundColor(timeBackgroundColor);
      timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeTextSize);
      timeView.setMaxLines(1);
      timeView.setPadding(timePaddingLeft, timePaddingTop, timePaddingRight, timePaddingBottom);

      final LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      lp.setMargins(0, 0, timeMarginRight, timeMarginBottom);
      lp.addRule(ALIGN_PARENT_BOTTOM);
      lp.addRule(ALIGN_PARENT_RIGHT);
      timeView.setLayoutParams(lp);
      timeView.setVisibility(GONE);

      addView(timeView);
   }

   @NonNull
   public TextView getTitleView() {
      return titleView;
   }

   @NonNull
   public TextView getTimeView() {
      return timeView;
   }

   @NonNull
   public ImageView getThumbnailView() {
      return thumbnailView;
   }

   public void clearThumbnail() {
      ThumbnailLoader.cancelThumbnailLoad(this);
      titleView.setVisibility(GONE);
      timeView.setVisibility(GONE);
      thumbnailView.setImageDrawable(null);
      isLoaded = false;
   }

   public void setTitleVisibility(@Visibility int visibility) {
      titleVisible = visibility == VISIBLE;
      if (isLoaded) {
         titleView.setVisibility(visibility);
      }
   }

   public void setTimeVisibility(@Visibility int visibility) {
      timeVisible = visibility == VISIBLE;
      if (isLoaded) {
         timeView.setVisibility(visibility);
      }
   }

   public void displayThumbnail(@Nullable String title, int length, @Nullable Bitmap thumbnail) {
      ThumbnailLoader.cancelThumbnailLoad(this);
      setThumbnailInfo(title, length);
      thumbnailView.setImageBitmap(thumbnail);
   }

   public void displayThumbnail(@Nullable String title, int length, @Nullable Drawable thumbnail) {
      ThumbnailLoader.cancelThumbnailLoad(this);
      setThumbnailInfo(title, length);
      thumbnailView.setImageDrawable(thumbnail);
   }

   public void loadThumbnail(@NonNull String url) {
      loadThumbnail(url, null, null);
   }

   public void loadThumbnail(@NonNull String url, @NonNull ThumbnailLoadingListener listener) {
      loadThumbnail(url, listener, null);
   }

   public void loadThumbnail(final @NonNull String url, final @Nullable ThumbnailLoadingListener listener, final @Nullable ImageLoader imageLoader) {
      ThumbnailLoader.loadThumbnail(this, url, minThumbnailSize, listener, imageLoader);
   }

   void setThumbnailInfo(@Nullable String title, int length) {
      titleView.setText(title);
      if (titleVisible) {
         titleView.setVisibility(VISIBLE);
      }

      timeView.setText(Utils.secondsToTime(length));
      if (timeVisible && length > 0) {
         timeView.setVisibility(VISIBLE);
      }

      isLoaded = true;
   }
}
