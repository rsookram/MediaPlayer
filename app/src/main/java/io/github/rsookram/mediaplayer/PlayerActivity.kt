package io.github.rsookram.mediaplayer

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.exo_player_control_view.*

class PlayerActivity : AppCompatActivity() {

    private val player by lazy { ExoPlayerFactory.newSimpleInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parser = IntentParser()
        val video = parser.parse(intent)
        if (video == null) {
            finish()
            return
        }

        enableImmersiveMode()

        setContentView(R.layout.activity_player)

        player_view.player = player

        decrease_speed.setOnClickListener {
            player.playbackParameters = PlaybackParameters(player.playbackParameters.speed - 0.1F)
        }
        increase_speed.setOnClickListener {
            player.playbackParameters = PlaybackParameters(player.playbackParameters.speed + 0.1F)
        }

        val sideGestureAreaWidth = resources.getDimension(R.dimen.side_gesture_area_width)

        // Start with the controls hidden
        player_view.showController()
        controls_bar.doOnPreDraw {
            controls_bar.y = player_view.height.toFloat()
        }

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                // Scrolling controls the position of the controls bar
                val newY = controls_bar.y - distanceY
                val playerHeight = player_view.height.toFloat()
                val barHeight = controls_bar.height.toFloat()
                controls_bar.y = newY.coerceIn(playerHeight - barHeight, playerHeight)

                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val x = e.x

                when {
                    // Tapping on the left rewinds the media
                    x < sideGestureAreaWidth -> {
                        player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0))
                    }
                    // Tapping on the right fast forwards the media
                    x > gesture_area.width - sideGestureAreaWidth -> {
                        val durationMs = player.duration
                        val desired = player.currentPosition + 10_000
                        player.seekTo(
                            if (durationMs != C.TIME_UNSET) minOf(durationMs, desired) else desired
                        )
                    }
                    // Tapping in the middle toggles the play/pause state
                    else -> {
                        player.playWhenReady = !player.playWhenReady
                    }
                }

                return true
            }
        })
        gesture_area.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val mediaSourceFactory = ExtractorMediaSource.Factory(dataSourceFactory)

        val mediaSource = mediaSourceFactory.createMediaSource(video.uri)

        player.prepare(mediaSource)
        player.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enableImmersiveMode()
        }
    }

    private fun enableImmersiveMode() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
