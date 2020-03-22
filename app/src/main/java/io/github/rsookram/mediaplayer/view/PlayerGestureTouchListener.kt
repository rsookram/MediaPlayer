package io.github.rsookram.mediaplayer.view

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import io.github.rsookram.mediaplayer.Event
import io.github.rsookram.mediaplayer.R

class PlayerGestureTouchListener(
    private val gestureArea: View,
    private val controlsAnimator: ControlsAnimator,
    private val isScrollEnabled: Boolean,
    private val pushEvent: (Event) -> Unit
) : View.OnTouchListener {

    private val context = gestureArea.context
    private val sideGestureAreaWidth = context.resources.getDimension(
        R.dimen.side_gesture_area_width
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent) = true

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (isScrollEnabled) {
                    controlsAnimator.handleScroll(distanceY)
                }
                return isScrollEnabled
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                pushEvent(when {
                    e.x < sideGestureAreaWidth ->
                        Event.Rewind
                    e.x > gestureArea.width - sideGestureAreaWidth ->
                        Event.FastForward
                    else ->
                        Event.TogglePlayPause
                })

                return true
            }
        }
    )

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val result = gestureDetector.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP) {
            controlsAnimator.settleToFinalPosition()
        }

        return result
    }
}
