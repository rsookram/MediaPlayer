package io.github.rsookram.mediaplayer.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.OneShotPreDrawListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.github.rsookram.mediaplayer.Event
import io.github.rsookram.mediaplayer.MediaType
import io.github.rsookram.mediaplayer.R

class PlayerView(container: ViewGroup, player: Player, title: String, mediaType: MediaType) {

    var onEvent: (Event) -> Unit = {}

    private val context = container.context

    private val playerView = LayoutInflater.from(context).inflate(
        R.layout.view_player,
        container,
        true
    ).findViewById(R.id.player_view) as StyledPlayerView

    private val controlsMode = when (mediaType) {
        MediaType.AUDIO -> ControlsMode.ALWAYS_SHOW
        MediaType.VIDEO -> ControlsMode.SCROLLABLE
    }

    private val gestureArea = playerView.findViewById<View>(R.id.gesture_area)

    private val titleLabel = playerView.findViewById<TextView>(R.id.title)
    private val playIndicator = playerView.findViewById<View>(R.id.play_indicator)
    private val controlsBar = playerView.findViewById<View>(R.id.controls_bar)

    private val playbackSpeed = playerView.findViewById<TextView>(R.id.playback_speed)
    private val decreaseSpeed = playerView.findViewById<View>(R.id.decrease_speed)
    private val increaseSpeed = playerView.findViewById<View>(R.id.increase_speed)

    private val controlsAnimator by lazy {
        ControlsAnimator(controlsBar, getPlayerHeight = { playerView.height })
    }

    init {
        playerView.player = player
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)

        decreaseSpeed.setOnClickListener { pushEvent(Event.DecreaseSpeed) }
        increaseSpeed.setOnClickListener { pushEvent(Event.IncreaseSpeed) }

        playerView.showController()

        if (controlsMode == ControlsMode.SCROLLABLE) {
            // Start with the controls hidden
            OneShotPreDrawListener.add(controlsBar) {
                controlsAnimator.setClosed()
            }
        }

        gestureArea.setOnTouchListener(
            PlayerGestureTouchListener(
                gestureArea,
                controlsAnimator,
                isScrollEnabled = controlsMode == ControlsMode.SCROLLABLE,
                pushEvent = ::pushEvent
            )
        )

        // Prevent touches on the controls bar from going through to the
        // gesture area
        controlsBar.setOnClickListener {}

        titleLabel.text = title
    }

    private fun pushEvent(e: Event) {
        onEvent(e)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        playIndicator.visibility = if (isPlaying) View.GONE else View.VISIBLE
    }

    fun setPlaybackSpeed(speed: Float) {
        val formattedSpeed = String.format("%.1f", speed)
        playbackSpeed.text = context.getString(
            R.string.playback_speed_multiplier, formattedSpeed
        )
    }

    fun toggleControls() {
        if (controlsMode == ControlsMode.ALWAYS_SHOW) return

        controlsAnimator.toggleDisplay()
    }
}

private enum class ControlsMode {
    SCROLLABLE, ALWAYS_SHOW,
}
