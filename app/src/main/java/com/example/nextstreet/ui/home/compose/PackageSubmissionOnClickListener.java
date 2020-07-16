package com.example.nextstreet.ui.home.compose;

import android.view.View;

public class PackageSubmissionOnClickListener implements View.OnClickListener{

    private String TAG;
    private ComposeFragment fragment;

    public PackageSubmissionOnClickListener(String TAG, String username, ComposeFragment fragment) {
        this.TAG = TAG;
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        fragment.checkPostable();
    }
}
