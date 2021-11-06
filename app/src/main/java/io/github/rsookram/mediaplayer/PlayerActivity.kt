package io.github.rsookram.mediaplayer

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.github.rsookram.mediaplayer.notification.DescriptionAdapter
import io.github.rsookram.mediaplayer.notification.ServiceNotificationListener
import io.github.rsookram.mediaplayer.notification.StopNotificationOnPauseListener
import io.github.rsookram.mediaplayer.view.PlayerView

private const val NOTIFICATION_ID = 1

class PlayerActivity : Activity() {

    private val player by lazy {
        ExoPlayer.Builder(this)
            .setTrackSelector(
                DefaultTrackSelector(this).apply {
                    // For some reason the video track needs to be disabled to
                    // disable subs
                    parameters = buildUponParameters()
                        .setRendererDisabled(C.TRACK_TYPE_VIDEO, true)
                        .build()
                }
            )
            .build()
    }

    private lateinit var view: PlayerView

    private lateinit var notificationManager: PlayerNotificationManager

    private var playbackSpeed = 1.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parser = IntentParser()
        val playbackRequest = parser.parse(intent)
        if (playbackRequest == null) {
            finish()
            return
        }

        enableImmersiveMode(window.decorView)

        val title = playbackRequest.uri.lastPathSegment ?: playbackRequest.uri.toString()

        val container = findViewById<ViewGroup>(android.R.id.content)
        view = PlayerView(container, player, title, playbackRequest.mediaType)

        view.onEvent = { event ->
            when (event) {
                Event.DecreaseSpeed -> decreaseSpeed(view)
                Event.IncreaseSpeed -> increaseSpeed(view)
                Event.Rewind -> rewind()
                Event.FastForward -> fastForward()
                Event.TogglePlayPause -> togglePlayPause()
            }
        }

        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                view.setIsPlaying(playWhenReady)
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED && playbackRequest.autoClose) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        })

        val mediaSource = createMediaSource(this, playbackRequest)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true

        notificationManager = PlayerNotificationManager.Builder(this, NOTIFICATION_ID, "media")
            .setChannelNameResourceId(R.string.channel_media_playback)
            .setMediaDescriptionAdapter(DescriptionAdapter(title))
            .setNotificationListener(ServiceNotificationListener(this))
            .build()

        player.addListener(StopNotificationOnPauseListener(stopNotification = {
            notificationManager.setPlayer(null)
        }))
    }

    private fun decreaseSpeed(view: PlayerView) {
        adjustPlaybackSpeed(view, -0.1F)
    }

    private fun increaseSpeed(view: PlayerView) {
        adjustPlaybackSpeed(view, +0.1F)
    }

    private fun adjustPlaybackSpeed(view: PlayerView, delta: Float) {
        playbackSpeed += delta
        player.playbackParameters = PlaybackParameters(playbackSpeed)

        view.setPlaybackSpeed(playbackSpeed)
    }

    private fun rewind() {
        if (!player.isCurrentMediaItemSeekable) return

        player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0))
    }

    private fun fastForward() {
        if (!player.isCurrentMediaItemSeekable) return

        val durationMs = player.duration
        val desired = player.currentPosition + 10_000
        player.seekTo(
            if (durationMs != C.TIME_UNSET) minOf(durationMs, desired) else desired
        )
    }

    private fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    override fun onStart() {
        super.onStart()
        notificationManager.setPlayer(null)
    }

    override fun onStop() {
        super.onStop()

        // If the player is set when finishing, it will get unset shortly after
        // in onDestroy. This causes Context.stopService to be called before
        // Service.startForeground, which will crash the app.
        if (!isFinishing) {
            notificationManager.setPlayer(player)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        player.release()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean =
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                rewind()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                fastForward()
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                decreaseSpeed(view)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                increaseSpeed(view)
                true
            }
            KeyEvent.KEYCODE_SPACE -> {
                togglePlayPause()
                true
            }
            KeyEvent.KEYCODE_MENU -> {
                view.toggleControls()
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode(window.decorView)
        }
    }
}
