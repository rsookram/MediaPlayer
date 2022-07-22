package io.github.rsookram.mediaplayer.notification

import android.app.Notification
import android.content.Context
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class ServiceNotificationListener(
    private val context: Context
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        val intent = NotificationService.newIntent(context, notificationId, notification)
        context.startForegroundService(intent)
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        context.stopService(NotificationService.newIntent(context))
    }
}
