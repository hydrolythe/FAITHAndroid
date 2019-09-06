package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates

abstract class AudioState(internal val context: AudioContext) {
    abstract fun onPlayPressed()
    abstract fun onPausePressed()
    abstract fun onStopPressed()
    abstract fun onRecordPressed()

    abstract val playButtonVisible: Int
    abstract val pauseButtonVisible: Int
    abstract val stopButtonVisible: Int
    abstract val recordButtonVisible: Int

    abstract fun release()
}
