package io.github.rsookram.mediaplayer

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

private const val HLS_MIME_TYPE = "application/vnd.apple.mpegurl"

class PlayerActivity : AppCompatActivity() {

    private val player by lazy { ExoPlayerFactory.newSimpleInstance(this) }

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

        val container = findViewById<ViewGroup>(android.R.id.content)
        val view = PlayerView(container, player)

        view.onEvent = { event ->
            when (event) {
                Event.DecreaseSpeed -> adjustPlaybackSpeed(view, -0.1F)
                Event.IncreaseSpeed -> adjustPlaybackSpeed(view, +0.1F)
                Event.Rewind -> {
                    player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0))
                }
                Event.FastForward -> {
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

        val appName = getString(R.string.app_name)
        val userAgent = playbackRequest.userAgent ?: Util.getUserAgent(this, appName)
        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val mediaSourceFactory = if (playbackRequest.mimeType == HLS_MIME_TYPE)
            HlsMediaSource.Factory(dataSourceFactory)
        else
            ExtractorMediaSource.Factory(dataSourceFactory)

        val mediaSource = mediaSourceFactory.createMediaSource(playbackRequest.uri)

        player.prepare(mediaSource)
        player.playWhenReady = true
    }

    private fun adjustPlaybackSpeed(view: PlayerView, delta: Float) {
        playbackSpeed += delta
        player.playbackParameters = PlaybackParameters(playbackSpeed)

        view.setPlaybackSpeed(playbackSpeed)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode(window.decorView)
        }
    }
}
