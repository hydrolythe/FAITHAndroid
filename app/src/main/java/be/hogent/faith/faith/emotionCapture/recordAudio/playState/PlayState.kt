package be.hogent.faith.faith.emotionCapture.recordAudio.playState

interface PlayState {
    fun onPlayPressed()
    fun onPausePressed()
    fun onStopPressed()
}
