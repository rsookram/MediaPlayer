package io.github.rsookram.mediaplayer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.media3.common.Player;

import java.util.function.Consumer;

import io.github.rsookram.mediaplayer.Event;
import io.github.rsookram.mediaplayer.R;

public class PlayerView {

    private Consumer<Event> onEvent;

    private final View playIndicator;
    private final TextView playbackSpeed;

    private final ControlsAnimator controlsAnimator;

    public PlayerView(ViewGroup container, Player player, String title) {
        androidx.media3.ui.PlayerView playerView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_player, container, true)
                .findViewById(R.id.player_view);

        View gestureArea = playerView.findViewById(R.id.gesture_area);

        TextView titleLabel = playerView.findViewById(R.id.title);
        playIndicator = playerView.findViewById(R.id.play_indicator);
        View controlsBar = playerView.findViewById(R.id.controls_bar);

        playbackSpeed = playerView.findViewById(R.id.playback_speed);
        View decreaseSpeed = playerView.findViewById(R.id.decrease_speed);
        View increaseSpeed = playerView.findViewById(R.id.increase_speed);

        controlsAnimator = new ControlsAnimator(controlsBar, playerView::getHeight);

        playerView.setPlayer(player);
        playerView.setShowBuffering(androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS);

        decreaseSpeed.setOnClickListener(v -> pushEvent(Event.DECREASE_SPEED));
        increaseSpeed.setOnClickListener(v -> pushEvent(Event.INCREASE_SPEED));

        playerView.showController();

        // Start with controls hidden
        controlsBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                controlsBar.getViewTreeObserver().removeOnPreDrawListener(this);
                controlsAnimator.setClosed();
                return true;
            }
        });

        gestureArea.setOnTouchListener(
                new PlayerGestureTouchListener(gestureArea, controlsAnimator, this::pushEvent)
        );

        // Prevent touches on the controls bar from going through to the gesture area.
        controlsBar.setOnClickListener(v -> {});

        titleLabel.setText(title);
    }

    private void pushEvent(Event e) {
        onEvent.accept(e);
    }

    public void setOnEvent(Consumer<Event> onEvent) {
        this.onEvent = onEvent;
    }

    public void setIsPlaying(boolean isPlaying) {
        playIndicator.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    public void setPlaybackSpeed(float speed) {
        playbackSpeed.setText(playbackSpeed.getResources().getString(
                R.string.playback_speed_multiplier, String.format("%.1f", speed)
        ));
    }

    public void toggleControls() {
        controlsAnimator.toggleDisplay();
    }
}
