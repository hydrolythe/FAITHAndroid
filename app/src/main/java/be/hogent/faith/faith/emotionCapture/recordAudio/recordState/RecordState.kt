package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

interface RecordState {
    fun onRecordPressed()
    fun onPausePressed()
    fun onStopPressed()
    fun onRestartPressed()
}