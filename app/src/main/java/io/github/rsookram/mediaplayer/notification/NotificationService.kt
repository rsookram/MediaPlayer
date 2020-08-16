package io.github.rsookram.mediaplayer.notification

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

private const val EXTRA_NOTIFICATION_ID = "notificationId"
private const val EXTRA_NOTIFICATION = "notification"

class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        require(notificationId > 0)

        startForeground(notificationId, intent.getParcelableExtra(EXTRA_NOTIFICATION))

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {

        fun newIntent(context: Context, notificationId: Int, notification: Notification) =
            newIntent(context)
                .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                .putExtra(EXTRA_NOTIFICATION, notification)

        fun newIntent(context: Context) =
            Intent(context, NotificationService::class.java)
    }
}
