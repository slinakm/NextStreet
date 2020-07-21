package com.example.nextstreet.ui;

import android.util.Log;

import com.example.nextstreet.models.PackageRequest;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class RequestQueryCallback implements FindCallback<PackageRequest> {

  private static final String TAG = RequestQueryCallback.class.getSimpleName();
  private final QueryResponder responder;

  public RequestQueryCallback(QueryResponder responder) {
    this.responder = responder;
  }

  @Override
  public void done(List<PackageRequest> posts, ParseException e) {
    if (posts != null) {
      Log.d(TAG, "done query: post size = " + posts.size());
      if (e != null) {
        Log.e(TAG, "queryPosts: Issue getting posts", e);
      }

      responder.respondToQuery(posts);
    }
  }
}
