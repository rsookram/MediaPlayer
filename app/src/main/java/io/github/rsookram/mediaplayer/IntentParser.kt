package io.github.rsookram.mediaplayer

import android.content.Intent

class IntentParser {

    fun parse(intent: Intent): PlaybackRequest? {
        val uri = intent.data ?: return null
        val mimeType = intent.type ?: return null

        val userAgent = cleanUserAgent(intent.getStringExtra("intent.extra.header.USER_AGENT"))

        return PlaybackRequest(uri, mimeType, userAgent)
    }

    private fun cleanUserAgent(userAgent: String?): String? {
        if (userAgent.isNullOrBlank()) {
            return null
        }

        if (userAgent.any { (it <= '\u001f' && it != '\t') || it >= '\u007f' }) {
            return null
        }

        return userAgent
    }
}
