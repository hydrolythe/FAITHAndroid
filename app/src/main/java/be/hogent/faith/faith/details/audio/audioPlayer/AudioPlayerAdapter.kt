package be.hogent.faith.faith.details.audio.audioPlayer

import java.io.File

interface AudioPlayerAdapter {
    fun release()
    val isPlaying: Boolean

    fun play()
    fun reset()
    fun pause()
    fun initializeProgressCallback()
    fun seekTo(position: Int)
    fun loadMedia(audioFile: File)
}
