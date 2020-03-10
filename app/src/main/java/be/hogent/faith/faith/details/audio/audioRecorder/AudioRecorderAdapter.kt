package be.hogent.faith.faith.details.audio.audioRecorder

interface AudioRecorderAdapter {
    fun release()
    fun record()
    fun stop()
    fun reset()
    fun pause()
}