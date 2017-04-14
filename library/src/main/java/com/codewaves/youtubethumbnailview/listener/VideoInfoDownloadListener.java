package com.codewaves.youtubethumbnailview.listener;

import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.VideoInfo;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface VideoInfoDownloadListener {
   void onDownloadFinished(@NonNull VideoInfo info);
   void onDownloadFailed(@NonNull Throwable error);
}
