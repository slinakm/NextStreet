package com.example.nextstreet.utilities;

import android.util.Log;
import android.view.View;

public class BackOnClickListener implements View.OnClickListener {

    BackResponder responder;

    public BackOnClickListener(BackResponder responder) {
        this.responder = responder;
    }

    @Override
    public void onClick(View view) {
        responder.goBack();
    }
}
