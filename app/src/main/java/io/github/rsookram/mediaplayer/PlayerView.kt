package io.github.rsookram.mediaplayer

import android.view.LayoutInflater
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

        root.decrease_speed.setOnClickListener { pushEvent(Event.DecreaseSpeed) }
        root.increase_speed.setOnClickListener { pushEvent(Event.IncreaseSpeed) }

        val controlsAnimator = ControlsAnimator(
            root.controls_bar, getPlayerHeight = { root.player_view.height }
        )

        // Start with the controls hidden
        root.player_view.showController()
        root.controls_bar.doOnPreDraw {
            controlsAnimator.setClosed()
        }

        root.gesture_area.setOnTouchListener(
            PlayerGestureTouchListener(root.gesture_area, controlsAnimator, ::pushEvent)
        )

        // Prevent touches on the controls bar from going through to the
        // gesture area
        root.controls_bar.setOnClickListener {}
    }

    private fun pushEvent(e: Event) {
        onEvent(e)
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
