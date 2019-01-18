package io.github.rsookram.mediaplayer

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.exo_player_control_view.view.*
import kotlinx.android.synthetic.main.view_player.view.*

class PlayerView(container: ViewGroup, player: Player) {

    var onEvent: (Event) -> Unit = {}

    private val context = container.context

    private val root = LayoutInflater.from(context)
        .inflate(R.layout.view_player, container, true)

    init {
        root.player_view.player = player

        root.decrease_speed.setOnClickListener { onEvent(Event.DecreaseSpeed) }
        root.increase_speed.setOnClickListener { onEvent(Event.IncreaseSpeed) }

        val controlsAnimator = ControlsAnimator(
            root.controls_bar, getPlayerHeight = { root.player_view.height }
        )

        // Start with the controls hidden
        root.player_view.showController()
        root.controls_bar.doOnPreDraw {
            controlsAnimator.setClosed()
        }

        val sideGestureAreaWidth = context.resources.getDimension(R.dimen.side_gesture_area_width)

        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                controlsAnimator.handleScroll(distanceY)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val x = e.x

                onEvent(when {
                    x < sideGestureAreaWidth ->
                        Event.Rewind
                    x > root.gesture_area.width - sideGestureAreaWidth ->
                        Event.FastForward
                    else ->
                        Event.TogglePlayPause
                })

                return true
            }
        })

        root.gesture_area.setOnTouchListener { _, event ->
            val result = gestureDetector.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                controlsAnimator.settleToFinalPosition()
            }

            result
        }


        // Prevent touches on the controls bar from going through to the
        // gesture area
        root.controls_bar.setOnClickListener {}
    }

    fun setIsPlaying(isPlaying: Boolean) {
        root.play_indicator.isGone = isPlaying
    }

    fun setPlaybackSpeed(speed: Float) {
        val formattedSpeed = String.format("%.1f", speed)
        root.playback_speed.text = context.getString(
            R.string.playback_speed_multiplier, formattedSpeed
        )
    }
}
