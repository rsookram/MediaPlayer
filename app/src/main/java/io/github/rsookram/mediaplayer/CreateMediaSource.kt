package io.github.rsookram.mediaplayer

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

fun createMediaSource(context: Context, uri: Uri): MediaSource {
    val factory = createMediaSourceFactory(context)

    return factory.createMediaSource(MediaItem.fromUri(uri))
}

private fun createMediaSourceFactory(
    context: Context,
): MediaSource.Factory {
    val dataSourceFactory = DefaultDataSource.Factory(context)

    return ProgressiveMediaSource.Factory(dataSourceFactory)
}
