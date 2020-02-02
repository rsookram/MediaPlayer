package io.github.rsookram.mediaplayer.notification

import android.app.PendingIntent
import android.graphics.Bitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class DescriptionAdapter(
    private val title: String
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun createCurrentContentIntent(player: Player): PendingIntent? = null

    override fun getCurrentContentText(player: Player): String? = null

    override fun getCurrentContentTitle(player: Player): String = title

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = null
}
