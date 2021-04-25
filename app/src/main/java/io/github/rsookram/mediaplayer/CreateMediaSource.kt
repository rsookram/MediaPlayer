package io.github.rsookram.mediaplayer

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

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
): MediaSourceFactory {
    val userAgent = playbackRequest.headers["user-agent"]
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent(userAgent)
        .setDefaultRequestProperties(playbackRequest.headers)
    val dataSourceFactory = DefaultDataSourceFactory(context, null, httpDataSourceFactory)

    return ProgressiveMediaSource.Factory(dataSourceFactory)
}
