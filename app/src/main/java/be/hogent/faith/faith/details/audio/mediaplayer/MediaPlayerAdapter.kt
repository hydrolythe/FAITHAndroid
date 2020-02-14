package be.hogent.faith.faith.details.audio.mediaplayer

import java.io.File

interface MediaPlayerAdapter {
    fun release()
    val isPlaying: Boolean

    fun play()
    fun reset()
    fun pause()
    fun initializeProgressCallback()
    fun seekTo(position: Int)
    fun loadMedia(audioFile: File)
}
