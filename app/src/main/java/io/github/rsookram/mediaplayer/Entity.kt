package io.github.rsookram.mediaplayer

import android.net.Uri

data class PlaybackRequest(
    val uri: Uri,
    val audioUri: Uri?,
    val mimeType: String,
    val headers: Map<String, String>,
    val autoClose: Boolean,
)
