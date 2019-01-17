package io.github.rsookram.mediaplayer

import android.content.Context
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

private const val HLS_MIME_TYPE = "application/vnd.apple.mpegurl"

fun createMediaSourceFactory(
    context: Context,
    playbackRequest: PlaybackRequest
): AdsMediaSource.MediaSourceFactory {
    val appName = context.getString(R.string.app_name)
    val userAgent = playbackRequest.userAgent ?: Util.getUserAgent(context, appName)
    val dataSourceFactory = DefaultDataSourceFactory(context, userAgent)

    return if (playbackRequest.mimeType == HLS_MIME_TYPE)
        HlsMediaSource.Factory(dataSourceFactory)
    else
        ExtractorMediaSource.Factory(dataSourceFactory)
}
