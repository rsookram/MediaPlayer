package io.github.rsookram.mediaplayer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;

import io.github.rsookram.mediaplayer.view.PlayerView;

public class PlayerActivity extends Activity {

    private ExoPlayer player;
    private PlayerView view;

    private float playbackSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }

        String title = uri.getLastPathSegment();
        if (title == null) {
            title = uri.toString();
        }

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        // For some reason the video track needs to be disabled to disable subs.
        trackSelector.setParameters(
                trackSelector.buildUponParameters().setRendererDisabled(C.TRACK_TYPE_VIDEO, true).build()
        );

        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setUsePlatformDiagnostics(false)
                .build();

        ViewGroup container = findViewById(android.R.id.content);
        view = new PlayerView(container, player, title);

        enableImmersiveMode();

        setPlaybackSpeed(view, loadSpeed());

        view.setOnEvent(event -> {
            switch (event) {
                case DECREASE_SPEED:
                    decreaseSpeed(view);
                    break;
                case INCREASE_SPEED:
                    increaseSpeed(view);
                    break;
                case REWIND:
                    rewind();
                    break;
                case FAST_FORWARD:
                    fastForward();
                    break;
                case TOGGLE_PLAY_PAUSE:
                    togglePlayPause();
                    break;
            }
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                view.setIsPlaying(playWhenReady);
            }
        });

        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);

        ProgressiveMediaSource mediaSource = factory.createMediaSource(MediaItem.fromUri(uri));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private void decreaseSpeed(PlayerView view) {
        setPlaybackSpeed(view, playbackSpeed - 0.25F);
    }

    private void increaseSpeed(PlayerView view) {
        setPlaybackSpeed(view, playbackSpeed + 0.25F);
    }

    private void setPlaybackSpeed(PlayerView view, float speed) {
        playbackSpeed = speed;
        player.setPlaybackParameters(new PlaybackParameters(playbackSpeed));

        view.setPlaybackSpeed(playbackSpeed);
        saveSpeed(speed);
    }

    private float loadSpeed() {
        return prefs().getFloat("speed", 1.0f);
    }

    private void saveSpeed(float speed) {
        prefs().edit().putFloat("speed", speed).apply();
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("settings", MODE_PRIVATE);
    }

    private void rewind() {
        if (!player.isCurrentMediaItemSeekable()) {
            return;
        }

        player.seekTo(Math.max(player.getCurrentPosition() - 10_000, 0));
    }

    private void fastForward() {
        if (!player.isCurrentMediaItemSeekable()) {
            return;
        }

        long durationMs = player.getDuration();
        long desired = player.getCurrentPosition() + 10_000;
        player.seekTo(durationMs != C.TIME_UNSET ? Math.min(durationMs, desired) : desired);
    }

    private void togglePlayPause() {
        player.setPlayWhenReady(!player.getPlayWhenReady());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                rewind();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                fastForward();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                decreaseSpeed(view);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                increaseSpeed(view);
                return true;
            case KeyEvent.KEYCODE_SPACE:
                togglePlayPause();
                return true;
            case KeyEvent.KEYCODE_MENU:
                view.toggleControls();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    private void enableImmersiveMode() {
        getWindow().setDecorFitsSystemWindows(false);

        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller == null) {
            return;
        }

        controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        controller.hide(WindowInsets.Type.systemBars());
    }
}
