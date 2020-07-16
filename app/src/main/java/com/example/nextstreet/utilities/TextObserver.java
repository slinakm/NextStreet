package com.example.nextstreet.utilities;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class TextObserver implements Observer<String> {
    private TextView tv;

    public TextObserver(TextView tv) {
        this.tv = tv;
    }

    @Override
    public void onChanged(@Nullable String s) {
        tv.setText(s);
    }
}
