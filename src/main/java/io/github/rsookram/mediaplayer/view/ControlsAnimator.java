package io.github.rsookram.mediaplayer.view;

import android.view.View;

import java.util.function.Supplier;

public class ControlsAnimator {

    private final View controls;
    private final Supplier<Integer> getPlayerHeight;

    public ControlsAnimator(View controls, Supplier<Integer> getPlayerHeight) {
        this.controls = controls;
        this.getPlayerHeight = getPlayerHeight;
    }

    public void toggleDisplay() {
        if (controls.getY() == getOpenedY()) {
            animateTo(getClosedY());
        } else if (controls.getY() == getClosedY()) {
            animateTo(getOpenedY());
        }
    }

    public void setClosed() {
        controls.setY(getClosedY());
    }

    public void handleScroll(float scrolledBy) {
        float newY = controls.getY() - scrolledBy;
        controls.setY(Math.min(Math.max(newY, getOpenedY()), getClosedY()));
    }

    public void settleToFinalPosition() {
        float halfwayPoint = getOpenedY() + ((getClosedY() - getOpenedY()) / 2);

        animateTo(
            controls.getY() > halfwayPoint ? getClosedY() : getOpenedY()
        );
    }

    private void animateTo(float y) {
        controls.animate().y(y);
    }

    private float getOpenedY() {
        return getClosedY() - controls.getHeight();
    }

    private float getClosedY() {
        return getPlayerHeight.get();
    }
}
