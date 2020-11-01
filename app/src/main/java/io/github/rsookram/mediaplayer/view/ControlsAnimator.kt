package io.github.rsookram.mediaplayer.view

import android.view.View

class ControlsAnimator(private val controls: View, private val getPlayerHeight: () -> Int) {

    fun toggleDisplay() {
        when (controls.y) {
            getOpenedY() -> animateTo(getClosedY())
            getClosedY() -> animateTo(getOpenedY())
        }
    }

    fun setClosed() {
        controls.y = getClosedY()
    }

    fun handleScroll(scrolledBy: Float) {
        val newY = controls.y - scrolledBy
        controls.y = newY.coerceIn(getOpenedY(), getClosedY())
    }

    fun settleToFinalPosition() {
        val halfwayPoint = getOpenedY() + ((getClosedY() - getOpenedY()) / 2)

        animateTo(
            if (controls.y > halfwayPoint) getClosedY() else getOpenedY()
        )
    }

    private fun animateTo(y: Float) {
        controls.animate().y(y)
    }

    private fun getOpenedY(): Float = getClosedY() - controls.height.toFloat()
    private fun getClosedY(): Float = getPlayerHeight().toFloat()
}
