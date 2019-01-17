package io.github.rsookram.mediaplayer

sealed class Event {
    object DecreaseSpeed : Event()
    object IncreaseSpeed : Event()

    object Rewind : Event()
    object FastForward : Event()
    object TogglePlayPause : Event()
}
