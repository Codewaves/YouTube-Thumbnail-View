package com.codewaves.youtubethumbnailview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codewaves.youtubethumbnailview.listener.SimpleThumbnailLoadingListener;
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

   private ImageView thumbnail;
   private TextView title;
   private TextView time;

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
   public @interface Visibility {}

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
      final int timeBackgroundColor = attr.getColor(R.styleable.ThumbnailView_youtube_timeBackgroundColor, Color.BLACK);
      final int timePaddingLeft = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingLeft, dpToPx(context, 5.0f));
      final int timePaddingRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingRight, dpToPx(context, 5.0f));
      final int timePaddingTop = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingTop, dpToPx(context, 0.0f));
      final int timePaddingBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timePaddingBottom, dpToPx(context, 0.0f));
      final int timeMarginBottom = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginBottom, dpToPx(context, 10.0f));
      final int timeMarginRight = attr.getDimensionPixelSize(R.styleable.ThumbnailView_youtube_timeMarginRight, dpToPx(context, 10.0f));
      final float timeTextSize = attr.getDimension(R.styleable.ThumbnailView_youtube_timeTextSize, getResources().getDimension(R.dimen.time_text_size));

      attr.recycle();


      // Add thumbnail image
      thumbnail = new ImageView(context);
      thumbnail.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
      title.setVisibility(GONE);

      addView(title);

      // Add video length
      time = new TextView(context);
      time.setTextColor(timeColor);
      time.setBackgroundColor(timeBackgroundColor);
      time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeTextSize);
      time.setMaxLines(1);
      time.setPadding(timePaddingLeft, timePaddingTop, timePaddingRight, timePaddingBottom);

      final LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      lp.setMargins(0, 0, timeMarginRight, timeMarginBottom);
      lp.addRule(ALIGN_PARENT_BOTTOM);
      lp.addRule(ALIGN_PARENT_RIGHT);
      time.setLayoutParams(lp);
      time.setVisibility(GONE);

      addView(time);
   }

   @NonNull
   public TextView getTitleView() {
      return title;
   }

   @NonNull
   public TextView getTimeView() {
      return time;
   }

   @NonNull
   public ImageView getThumbnailView() {
      return thumbnail;
   }

   public void clearThumbnail() {
      title.setVisibility(GONE);
      time.setVisibility(GONE);
      thumbnail.setImageDrawable(null);
      isLoaded = false;
   }

   public void setTitleVisibility(@Visibility int visibility) {
      titleVisible = visibility == VISIBLE;
      if (isLoaded) {
         title.setVisibility(visibility);
      }
   }

   public void setTimeVisibility(@Visibility int visibility) {
      timeVisible = visibility == VISIBLE;
      if (isLoaded) {
         time.setVisibility(visibility);
      }
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
            if (titleVisible) {
               title.setVisibility(VISIBLE);
            }

            time.setText(Utils.secondsToTime(info.getLength()));
            if (timeVisible && info.getLength() > 0) {
               time.setVisibility(VISIBLE);
            }

            loadThumbnailImage(info.getThumbnailUrl(), imageLoader);

            isLoaded = true;
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
