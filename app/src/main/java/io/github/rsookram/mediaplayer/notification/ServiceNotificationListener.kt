package io.github.rsookram.mediaplayer.notification

import android.app.Notification
import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class ServiceNotificationListener(
    private val context: Context
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationStarted(notificationId: Int, notification: Notification) {
        val intent = NotificationService.newIntent(context, notificationId, notification)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onNotificationCancelled(notificationId: Int) {
        context.stopService(NotificationService.newIntent(context))
    }
}
