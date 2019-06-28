package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStateStopped(
    context: RecordingContext,
    recorder: MediaRecorder,
    tempFileProvider: TempFileProvider
) : RecordState(context, recorder, tempFileProvider) {

    override fun onRecordPressed() {
        reinitialiseRecorder()
        recorder.start()

        Log.d(TAG, "Stopped->Recording")
        context.goToRecordState(
            RecordStateRecording(
                context,
                recorder,
                tempFileProvider
            )
        )
    }

    override fun onPausePressed() {
        Log.d(TAG, "Stopped->Stopped: Can't stop a paused recording")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Stopped->Stopped: Recorder was already stopped")
    }

    override fun onRestartPressed() {
        reinitialiseRecorder()
        context.goToRecordState(RecordStateInitial(context, tempFileProvider))
        Log.d(TAG, "Stopped -> Initial")
    }
}