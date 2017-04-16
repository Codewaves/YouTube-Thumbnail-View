package com.codewaves.youtubethumbnailview;

/**
 * Created by Sergej Kravcenko on 4/16/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

abstract class CancellableTask implements Runnable {
   private boolean canceled;
   private boolean finished;

   boolean isCanceled() {
      return canceled;
   }

   public boolean isFinished() {
      return finished;
   }

   @Override
   public void run() {
   }

   void cancel() {
      if (canceled || finished) {
         return;
      }

      onCancel();
      canceled = true;
   }

   void finish() {
      finished = true;
   }

   abstract void onCancel();
}
