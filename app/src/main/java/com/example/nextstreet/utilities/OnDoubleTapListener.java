package com.example.nextstreet.utilities;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnDoubleTapListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public OnDoubleTapListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction()
                == MotionEvent.ACTION_UP) {
            view.performClick();
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            OnDoubleTapListener.this.onDoubleTap(e);
            return super.onDoubleTap(e);
        }
    }

    public void onDoubleTap(MotionEvent e) {
        // To be overridden when implementing listener
    }
}
