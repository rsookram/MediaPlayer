package io.github.rsookram.mediaplayer

import android.view.*
import androidx.core.view.doOnPreDraw
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

        // Start with the controls hidden
        root.player_view.showController()
        root.controls_bar.doOnPreDraw {
            root.controls_bar.y = root.player_view.height.toFloat()
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
                // Scrolling controls the position of the controls bar
                val newY = root.controls_bar.y - distanceY
                val barHeight = root.controls_bar.height.toFloat()

                val closedPosition = root.player_view.height.toFloat()
                val openPosition = closedPosition - barHeight

                root.controls_bar.y = newY.coerceIn(openPosition, closedPosition)

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

            // When gesture finishes, ensure that the controls settle at a
            // terminal position
            if (event.action == MotionEvent.ACTION_UP) {
                val closedPosition = root.player_view.height.toFloat()
                val openPosition = closedPosition - root.controls_bar.height.toFloat()

                val halfwayPoint = openPosition + ((closedPosition - openPosition) / 2)
                if (root.controls_bar.y > halfwayPoint) {
                    root.controls_bar.animate().y(closedPosition)
                } else {
                    root.controls_bar.animate().y(openPosition)
                }
            }

            result
        }


        // Prevent touches on the controls bar from going through to the
        // gesture area
        root.controls_bar.setOnClickListener {}
    }

    fun setIsPlaying(isPlaying: Boolean) {
        val visibility = if (isPlaying) View.GONE else View.VISIBLE
        root.play_indicator.visibility = visibility
    }

    fun setPlaybackSpeed(speed: Float) {
        val formattedSpeed = String.format("%.1f", speed)
        root.playback_speed.text = context.getString(
            R.string.playback_speed_multiplier, formattedSpeed
        )
    }
}

sealed class Event {
    object DecreaseSpeed : Event()
    object IncreaseSpeed : Event()

    object Rewind : Event()
    object FastForward : Event()
    object TogglePlayPause : Event()
}
