package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStateInitial(
    context: RecordingContext,
    tempFileProvider: TempFileProvider
) : RecordState(context, MediaRecorder(), tempFileProvider) {

    init {
        initialiseRecorder()
        Log.d(TAG, "Recorder initialised")
    }

    override fun onRecordPressed() {
        recorder.start()
        context.goToRecordState(RecordStateRecording(context, recorder, tempFileProvider))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Initial -> Initial: Can't pause a recording that hasn't started yet.")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Initial -> Initial: can't stop a recording that hasn't started yet.")
    }

    override fun onRestartPressed() {
        reinitialiseRecorder()
        Log.d(TAG, "Initial -> Initial: can't restart a recording that hasn't started yet.")
    }
}