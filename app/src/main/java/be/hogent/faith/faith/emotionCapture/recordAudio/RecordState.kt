package be.hogent.faith.faith.emotionCapture.recordAudio

interface RecordState {
    fun onRecordPressed()
    fun onPausePressed()
    fun onStopPressed()
    fun onPlayPressed()
    fun onRestartPressed()
}