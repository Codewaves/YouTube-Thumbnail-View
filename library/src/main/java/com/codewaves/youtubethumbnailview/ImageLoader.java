package com.codewaves.youtubethumbnailview;

import android.widget.ImageView;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface ImageLoader {
   void load(String url, ImageView imageView);
}
