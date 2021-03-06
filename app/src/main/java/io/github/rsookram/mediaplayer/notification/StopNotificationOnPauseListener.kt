package io.github.rsookram.mediaplayer.notification

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.google.android.exoplayer2.Player

private const val DELAY_BEFORE_STOP_MS = 60 * 1000L // 1 min

class StopNotificationOnPauseListener(
    private val stopNotification: () -> Unit
) : Player.EventListener {

    private val handler = Handler(Looper.getMainLooper())

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        handler.removeCallbacksAndMessages(null)

        if (playWhenReady) {
            return
        }

        handler.postDelayed(DELAY_BEFORE_STOP_MS) {
            stopNotification()
        }
    }
}
