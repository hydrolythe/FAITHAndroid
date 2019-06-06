package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

interface RecordingContext {
    fun goToState(newState: RecordState)
}