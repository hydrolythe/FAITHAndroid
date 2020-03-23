package be.hogent.faith.faith.details.audio.audioPlayer

interface PlaybackInfoListener {

    enum class PlaybackState {
        INVALID,
        PLAYING,
        PAUSED,
        RESET,
        COMPLETED
    }

    fun onStateChanged(state: PlaybackState)
    fun onLogUpdated(formattedMessage: String?)
    fun onDurationChanged(duration: Int)
    fun onPositionChanged(position: Int)
    fun onPlaybackCompleted()
}