package io.github.rsookram.mediaplayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

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

    return ProgressiveMediaSource.Factory(dataSourceFactory)
}
