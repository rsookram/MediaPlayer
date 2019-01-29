package io.github.rsookram.mediaplayer

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.github.rsookram.mediaplayer.notification.DescriptionAdapter
import io.github.rsookram.mediaplayer.notification.ServiceNotificationListener
import io.github.rsookram.mediaplayer.notification.StopNotificationOnPauseListener
import io.github.rsookram.mediaplayer.view.PlayerView

private const val NOTIFICATION_ID = 1

class PlayerActivity : AppCompatActivity() {

    private val player by lazy { ExoPlayerFactory.newSimpleInstance(this) }
    private val mediaSession by lazy { MediaSessionCompat(this, "media") }
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

        val mediaSourceFactory = createMediaSourceFactory(this, playbackRequest)
        val mediaSource = mediaSourceFactory.createMediaSource(playbackRequest.uri)

        player.prepare(mediaSource)
        player.playWhenReady = true

        MediaSessionConnector(mediaSession).setPlayer(player, null)
        mediaSession.isActive = true

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this, "media", R.string.channel_media_playback, NOTIFICATION_ID, DescriptionAdapter(title)
        ).apply {
            setNotificationListener(ServiceNotificationListener(this@PlayerActivity))
        }

        player.addListener(StopNotificationOnPauseListener(stopNotification = {
            notificationManager.setPlayer(null)
        }))
    }

    private fun adjustPlaybackSpeed(view: PlayerView, delta: Float) {
        playbackSpeed += delta
        player.playbackParameters = PlaybackParameters(playbackSpeed)

        view.setPlaybackSpeed(playbackSpeed)
    }

    override fun onStart() {
        super.onStart()
        notificationManager.setPlayer(null)
    }

    override fun onStop() {
        super.onStop()
        notificationManager.setPlayer(player)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        player.release()
        mediaSession.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode(window.decorView)
        }
    }
}
