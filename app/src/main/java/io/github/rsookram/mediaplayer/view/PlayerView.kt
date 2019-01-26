package io.github.rsookram.mediaplayer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import io.github.rsookram.mediaplayer.Event
import io.github.rsookram.mediaplayer.MediaType
import io.github.rsookram.mediaplayer.R
import kotlinx.android.synthetic.main.exo_player_control_view.view.*
import kotlinx.android.synthetic.main.view_player.view.*

class PlayerView(container: ViewGroup, player: Player, mediaType: MediaType) {

    var onEvent: (Event) -> Unit = {}

    private val context = container.context

    private val root = LayoutInflater.from(context)
        .inflate(R.layout.view_player, container, true)

    init {
        val controlsMode = when (mediaType) {
            MediaType.AUDIO -> ControlsMode.ALWAYS_SHOW
            MediaType.VIDEO -> ControlsMode.SCROLLABLE
        }

        root.player_view.player = player
        root.player_view.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)

        root.decrease_speed.setOnClickListener { pushEvent(Event.DecreaseSpeed) }
        root.increase_speed.setOnClickListener { pushEvent(Event.IncreaseSpeed) }

        val controlsAnimator = ControlsAnimator(
            root.controls_bar, getPlayerHeight = { root.player_view.height }
        )

        root.player_view.showController()

        if (controlsMode == ControlsMode.SCROLLABLE) {
            // Start with the controls hidden
            root.controls_bar.doOnPreDraw {
                controlsAnimator.setClosed()
            }
        }

        root.gesture_area.setOnTouchListener(
            PlayerGestureTouchListener(
                root.gesture_area,
                controlsAnimator,
                isScrollEnabled = controlsMode == ControlsMode.SCROLLABLE,
                pushEvent = ::pushEvent
            )
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

private enum class ControlsMode {
    SCROLLABLE, ALWAYS_SHOW
}
