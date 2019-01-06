package io.github.rsookram.mediaplayer

import android.content.Intent

class IntentParser {

    fun parse(intent: Intent): Video? {
        val uri = intent.data ?: return null
        val mimeType = intent.type ?: return null

        return Video(uri, mimeType)
    }
}
