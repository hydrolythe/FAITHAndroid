package be.hogent.faith.faith.details.audio.audioRecorder

interface RecordingInfoListener {

    enum class RecordingState {
        INVALID,
        RECORDING,
        PAUSED,
        STOPPED,
        RESET
    }

    fun onStateChanged(state: RecordingState)
    fun onRecordingDurationChanged(duration: Long)
}