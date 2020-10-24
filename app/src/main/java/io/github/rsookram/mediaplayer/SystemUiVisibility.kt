package io.github.rsookram.mediaplayer

import android.view.View

fun enableImmersiveMode(view: View) {
    // TODO: Replace with WindowInsetsControllerCompat when released in androidx.core
    @Suppress("DEPRECATION")
    view.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}
