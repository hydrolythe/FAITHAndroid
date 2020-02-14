package be.hogent.faith.faith.details.audio.mediaplayer

/**
 * Allows [MediaPlayerHolder] to report media playback duration and progress updates to
 * the [MainActivity].
 */
interface PlaybackInfoListener {
    fun onLogUpdated(formattedMessage: String?)
    fun onDurationChanged(duration: Int)
    fun onPositionChanged(position: Int)
    fun onPlaybackCompleted()
}