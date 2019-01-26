package io.github.rsookram.mediaplayer

import android.content.Intent
import androidx.core.os.bundleOf
import java.util.*

class IntentParser {

    fun parse(intent: Intent): PlaybackRequest? {
        val uri = intent.data ?: return null
        val mimeType = intent.type ?: return null

        val mediaType = determineMediaType(mimeType)

        val headersBundle = intent.getBundleExtra("intent.extra.headers") ?: bundleOf()
        val headers = headersBundle.keySet()
            .associateWith(headersBundle::getString)
            .filter { (k, v) -> isValidHeader(k, v) }
            .mapKeys { (k, _) -> k.toLowerCase(Locale.US) }

        return PlaybackRequest(uri, mimeType, mediaType, headers)
    }

    private fun determineMediaType(mimeType: String): MediaType =
        if (mimeType.startsWith("audio/")) MediaType.AUDIO else MediaType.VIDEO

    private fun isValidHeader(name: String, value: String?): Boolean {
        if (name.isBlank() || value.isNullOrBlank()) {
            return false
        }

        if (name.any { it <= '\u0020' || it >= '\u007f' }) {
            return false
        }

        if (value.any { (it <= '\u001f' && it != '\t') || it >= '\u007f' }) {
            return false
        }

        return true
    }
}
