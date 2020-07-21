package com.example.nextstreet.ui.home;

import android.util.Log;

import com.example.nextstreet.models.PackageRequest;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class RequestQueryCallback implements FindCallback<PackageRequest> {

  private final String TAG = RequestQueryCallback.class.getSimpleName();
  private final HomeFragment fragment;

  protected RequestQueryCallback(HomeFragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public void done(List<PackageRequest> posts, ParseException e) {
    if (posts != null) {
      Log.d(TAG, "done query: post size = " + posts.size());
      if (e != null) {
        Log.e(TAG, "queryPosts: Issue getting posts", e);
      }

      if (posts.size() != 0) {
        PackageRequest request = posts.get(0);
        Log.i(TAG, "received post" + request);

        fragment.respondToQuery(request);
      }
    }
  }
}
