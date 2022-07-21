package io.github.rsookram.mediaplayer

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

private const val HLS_MIME_TYPE = "application/vnd.apple.mpegurl"

fun createMediaSource(context: Context, playbackRequest: PlaybackRequest): MediaSource {
    val factory = createMediaSourceFactory(context, playbackRequest)

    val videoMediaSource = factory.createMediaSource(MediaItem.fromUri(playbackRequest.uri))
    if (playbackRequest.audioUri == null) {
        return videoMediaSource
    }

    return MergingMediaSource(
        videoMediaSource,
        factory.createMediaSource(MediaItem.fromUri(playbackRequest.audioUri))
    )
}

private fun createMediaSourceFactory(
    context: Context,
    playbackRequest: PlaybackRequest,
): MediaSource.Factory {
    val userAgent = playbackRequest.headers["user-agent"]
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent(userAgent)
        .setDefaultRequestProperties(playbackRequest.headers)
    val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

    return if (playbackRequest.mimeType == HLS_MIME_TYPE) {
        HlsMediaSource.Factory(dataSourceFactory)
    } else {
        ProgressiveMediaSource.Factory(dataSourceFactory)
    }
}
