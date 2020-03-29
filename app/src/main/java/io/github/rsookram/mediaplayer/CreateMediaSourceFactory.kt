package io.github.rsookram.mediaplayer

import android.content.Context
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

fun createMediaSourceFactory(
    context: Context,
    playbackRequest: PlaybackRequest
): MediaSourceFactory {
    val appName = context.getString(R.string.app_name)
    val userAgent = playbackRequest.headers["user-agent"] ?: Util.getUserAgent(context, appName)
    val httpDataSourceFactory = DefaultHttpDataSourceFactory(userAgent).apply {
        defaultRequestProperties.set(playbackRequest.headers)
    }
    val dataSourceFactory = DefaultDataSourceFactory(context, null, httpDataSourceFactory)

    return ProgressiveMediaSource.Factory(dataSourceFactory)
}
