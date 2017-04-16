package com.codewaves.youtubethumbnailview.listener;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface ThumbnailLoadingListener {
   void onLoadingStarted(@NonNull String url, @NonNull View view);
   void onLoadingComplete(@NonNull String url, @NonNull View view);
   void onLoadingCanceled(@NonNull String url, @NonNull View view);
   void onLoadingFailed(@NonNull String url, @NonNull View view, Throwable error);
}
