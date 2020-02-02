package io.github.rsookram.mediaplayer

import android.content.Context
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

private const val HLS_MIME_TYPE = "application/vnd.apple.mpegurl"

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

    return if (playbackRequest.mimeType == HLS_MIME_TYPE)
        HlsMediaSource.Factory(dataSourceFactory)
    else
        ProgressiveMediaSource.Factory(dataSourceFactory)
}
