package com.example.nextstreet.utilities;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarOnClickListener implements View.OnClickListener {
    private String s;

    public SnackbarOnClickListener(String s) {
        this.s = s;
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
