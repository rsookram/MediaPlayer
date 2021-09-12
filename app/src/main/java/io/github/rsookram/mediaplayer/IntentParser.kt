package io.github.rsookram.mediaplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import java.util.*

class IntentParser {

    fun parse(intent: Intent): PlaybackRequest? {
        val uri = intent.data ?: return null
        val audioUri = intent.getParcelableExtra<Uri>("intent.extra.uri.audio")
        val mimeType = intent.type ?: return null

        val mediaType = determineMediaType(mimeType)

        val headersBundle = intent.getBundleExtra("intent.extra.headers") ?: bundleOf()
        val headers = parseHeaders(headersBundle)

        val autoClose = intent.getBooleanExtra("intent.extra.autoclose", false)

        return PlaybackRequest(uri, audioUri, mimeType, mediaType, headers, autoClose)
    }

    private fun determineMediaType(mimeType: String): MediaType =
        if (mimeType.startsWith("audio/")) MediaType.AUDIO else MediaType.VIDEO

    private fun parseHeaders(bundle: Bundle): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        for (k in bundle.keySet()) {
            val value = bundle.getString(k)
            if (value != null) {
                headers[k] = value
            }
        }

        return headers
            .filter { (k, v) -> isValidHeader(k, v) }
            .mapKeys { (k, _) -> k.lowercase(Locale.US) }
    }

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
