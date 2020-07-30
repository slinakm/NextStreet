package com.example.nextstreet.compose;

import com.parse.ParseUser;

public interface ThreadCompleteListener {
  void notifyOfThreadComplete(final Runnable runnable, ParseUser driver);
}
