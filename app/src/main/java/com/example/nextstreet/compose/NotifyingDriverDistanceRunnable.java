package com.example.nextstreet.compose;

import android.util.Log;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class NotifyingDriverDistanceRunnable implements Runnable{

  private final String TAG = NotifyingDriverDistanceRunnable.class.getSimpleName();
  private final Set<ThreadCompleteListener> listeners =
      new CopyOnWriteArraySet<ThreadCompleteListener>();

  public void addListener(final ThreadCompleteListener listener) {
    listeners.add(listener);
  }

  public void removeListener(final ThreadCompleteListener listener) {
    listeners.remove(listener);
  }

  private void notifyListeners() {
    for (ThreadCompleteListener listener : listeners) {
      Log.i(TAG, "notifyListeners: notifying " + listener.getClass().getSimpleName());
      listener.notifyOfThreadComplete(this);
    }
  }

  @Override
  public final void run() {
    try {
      doRun();
    } finally {
      notifyListeners();
    }
  }

  public abstract void doRun();
}
