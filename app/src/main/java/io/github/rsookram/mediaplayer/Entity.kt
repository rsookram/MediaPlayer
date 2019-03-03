package io.github.rsookram.mediaplayer

import android.net.Uri

data class PlaybackRequest(
    val uri: Uri,
    val mimeType: String,
    val mediaType: MediaType,
    val headers: Map<String, String>
)

enum class MediaType {
    AUDIO, VIDEO
}