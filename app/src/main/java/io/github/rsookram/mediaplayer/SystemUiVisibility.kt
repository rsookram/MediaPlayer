package io.github.rsookram.mediaplayer

import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController

fun enableImmersiveMode(window: Window) {
    window.setDecorFitsSystemWindows(false)

    val controller = window.insetsController ?: return
    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(WindowInsets.Type.systemBars())
}
