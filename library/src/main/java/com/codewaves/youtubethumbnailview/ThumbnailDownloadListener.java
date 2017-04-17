package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface ThumbnailDownloadListener {
   void onDownloadFinished(@NonNull VideoInfo info, @Nullable Bitmap bitmap);
   void onDownloadFailed(@NonNull Throwable error);
}
