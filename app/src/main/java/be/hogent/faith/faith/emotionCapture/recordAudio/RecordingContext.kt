package be.hogent.faith.faith.emotionCapture.recordAudio

import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordState

interface RecordingContext {
    fun setState(newState: RecordState)
}