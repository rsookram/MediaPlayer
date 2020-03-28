package io.github.rsookram.mediaplayer

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.github.rsookram.mediaplayer.notification.DescriptionAdapter
import io.github.rsookram.mediaplayer.notification.ServiceNotificationListener
import io.github.rsookram.mediaplayer.notification.StopNotificationOnPauseListener
import io.github.rsookram.mediaplayer.view.PlayerView

private const val NOTIFICATION_ID = 1

class PlayerActivity : Activity() {

    private val player by lazy {
        SimpleExoPlayer.Builder(this)
            .setTrackSelector(
                DefaultTrackSelector(this).apply {
                    // For some reason the video track needs to be disabled to
                    // disable subs
                    parameters = buildUponParameters()
                        .setRendererDisabled(C.TRACK_TYPE_VIDEO, true)
                        .clearSelectionOverrides()
                        .build()
                }
            )
            .build()
    }
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
        val view = PlayerView(container, player, title, playbackRequest.mediaType)

        view.onEvent = { event ->
            when (event) {
                Event.DecreaseSpeed -> adjustPlaybackSpeed(view, -0.1F)
                Event.IncreaseSpeed -> adjustPlaybackSpeed(view, +0.1F)
                Event.Rewind -> if (player.isCurrentWindowSeekable) {
                    player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0))
                }
                Event.FastForward -> if (player.isCurrentWindowSeekable) {
                    val durationMs = player.duration
                    val desired = player.currentPosition + 10_000
                    player.seekTo(
                        if (durationMs != C.TIME_UNSET) minOf(durationMs, desired) else desired
                    )
                }
                Event.TogglePlayPause -> player.playWhenReady = !player.playWhenReady
            }
        }

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                view.setIsPlaying(playWhenReady)
            }
        })

        val mediaSource = createMediaSource(playbackRequest)

        player.prepare(mediaSource)
        player.playWhenReady = true

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            "media",
            R.string.channel_media_playback,
            0,
            NOTIFICATION_ID,
            DescriptionAdapter(title),
            ServiceNotificationListener(this@PlayerActivity)
        )

        player.addListener(StopNotificationOnPauseListener(stopNotification = {
            notificationManager.setPlayer(null)
        }))
    }

    private fun createMediaSource(playbackRequest: PlaybackRequest): MediaSource {
        val mediaSourceFactory = createMediaSourceFactory(this, playbackRequest)

        return if (playbackRequest.audioUri != null) {
            MergingMediaSource(
                mediaSourceFactory.createMediaSource(playbackRequest.uri),
                mediaSourceFactory.createMediaSource(playbackRequest.audioUri)
            )
        } else {
            mediaSourceFactory.createMediaSource(playbackRequest.uri)
        }
    }

    private fun adjustPlaybackSpeed(view: PlayerView, delta: Float) {
        playbackSpeed += delta
        player.setPlaybackParameters(PlaybackParameters(playbackSpeed))

        view.setPlaybackSpeed(playbackSpeed)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode(window.decorView)
        }
    }
}
