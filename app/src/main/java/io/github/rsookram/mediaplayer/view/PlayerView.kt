package io.github.rsookram.mediaplayer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.github.rsookram.mediaplayer.Event
import io.github.rsookram.mediaplayer.MediaType
import io.github.rsookram.mediaplayer.R
import io.github.rsookram.mediaplayer.databinding.ExoPlayerControlViewBinding
import io.github.rsookram.mediaplayer.databinding.ViewPlayerBinding

class PlayerView(container: ViewGroup, player: Player, title: String, mediaType: MediaType) {

    var onEvent: (Event) -> Unit = {}

    private val context = container.context

    private val playerView = ViewPlayerBinding
        .inflate(LayoutInflater.from(context), container, true)
        .playerView

    private val root = ExoPlayerControlViewBinding.bind(playerView)

    private val controlsMode = when (mediaType) {
        MediaType.AUDIO -> ControlsMode.ALWAYS_SHOW
        MediaType.VIDEO -> ControlsMode.SCROLLABLE
    }

    private val controlsAnimator by lazy {
        ControlsAnimator(root.controlsBar, getPlayerHeight = { playerView.height })
    }

    init {
        playerView.player = player
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)

        root.decreaseSpeed.setOnClickListener { pushEvent(Event.DecreaseSpeed) }
        root.increaseSpeed.setOnClickListener { pushEvent(Event.IncreaseSpeed) }

        playerView.showController()

        if (controlsMode == ControlsMode.SCROLLABLE) {
            // Start with the controls hidden
            root.controlsBar.doOnPreDraw {
                controlsAnimator.setClosed()
            }
        }

        root.gestureArea.setOnTouchListener(
            PlayerGestureTouchListener(
                root.gestureArea,
                controlsAnimator,
                isScrollEnabled = controlsMode == ControlsMode.SCROLLABLE,
                pushEvent = ::pushEvent
            )
        )

        // Prevent touches on the controls bar from going through to the
        // gesture area
        root.controlsBar.setOnClickListener {}

        root.title.text = title
    }

    private fun pushEvent(e: Event) {
        onEvent(e)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        root.playIndicator.isGone = isPlaying
    }

    fun setPlaybackSpeed(speed: Float) {
        val formattedSpeed = String.format("%.1f", speed)
        root.playbackSpeed.text = context.getString(
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
