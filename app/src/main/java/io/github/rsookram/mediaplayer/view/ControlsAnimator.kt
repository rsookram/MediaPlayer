package io.github.rsookram.mediaplayer.view

import android.view.View

class ControlsAnimator(private val controls: View, private val getPlayerHeight: () -> Int) {

    fun setClosed() {
        controls.y = getClosedY()
    }

    fun handleScroll(scrolledBy: Float) {
        val newY = controls.y - scrolledBy
        controls.y = newY.coerceIn(getOpenedY(), getClosedY())
    }

    fun settleToFinalPosition() {
        val halfwayPoint = getOpenedY() + ((getClosedY() - getOpenedY()) / 2)

        controls.animate()
            .y(if (controls.y > halfwayPoint) getClosedY() else getOpenedY())
    }

    private fun getOpenedY(): Float = getClosedY() - controls.height.toFloat()
    private fun getClosedY(): Float = getPlayerHeight().toFloat()
}
