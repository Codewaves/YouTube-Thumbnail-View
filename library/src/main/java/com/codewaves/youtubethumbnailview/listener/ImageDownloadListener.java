package com.codewaves.youtubethumbnailview.listener;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface ImageDownloadListener {
   void onDownloadFinished(@NonNull Bitmap image);
   void onDownloadFailed(@NonNull Throwable error);
}
