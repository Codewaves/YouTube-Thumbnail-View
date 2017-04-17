package com.codewaves.youtubethumbnailview;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface ImageLoader {
   @Nullable
   Bitmap load(String url) throws IOException;
}
