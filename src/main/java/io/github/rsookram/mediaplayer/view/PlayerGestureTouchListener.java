package io.github.rsookram.mediaplayer.view;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.function.Consumer;

import io.github.rsookram.mediaplayer.Event;
import io.github.rsookram.mediaplayer.R;

public class PlayerGestureTouchListener implements View.OnTouchListener {

    private final ControlsAnimator controlsAnimator;

    private final float sideGestureAreaWidth;

    private final GestureDetector gestureDetector;

    public PlayerGestureTouchListener(
            View gestureArea,
            ControlsAnimator controlsAnimator,
            Consumer<Event> pushEvent
    ) {
        this.controlsAnimator = controlsAnimator;

        this.sideGestureAreaWidth = gestureArea.getResources().getDimension(R.dimen.side_gesture_area_width);

        this.gestureDetector = new GestureDetector(gestureArea.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                controlsAnimator.handleScroll(distanceY);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (e.getX() < sideGestureAreaWidth) {
                    pushEvent.accept(Event.REWIND);
                } else if (e.getX() > gestureArea.getWidth() - sideGestureAreaWidth) {
                    pushEvent.accept(Event.FAST_FORWARD);
                } else {
                    pushEvent.accept(Event.TOGGLE_PLAY_PAUSE);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            controlsAnimator.settleToFinalPosition();
        }

        return result;
    }
}
