package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStatePaused(
    context: RecordingContext,
    recorder: MediaRecorder,
    tempFileProvider: TempFileProvider
) : RecordState(context, recorder, tempFileProvider) {

    override fun onRecordPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.resume()
            Log.d(TAG, "Paused -> Recording")
            context.goToRecordState(RecordStateRecording(context, recorder, tempFileProvider))
        } else {
            Log.d(TAG, "Resuming the recorder is not supported on a device with this API level")
            Log.d(TAG, "How did you even pause the recording?")
        }
    }

    override fun onPausePressed() {
        Log.d(TAG, "Recorder was already paused, won't pause again")
    }

    override fun onStopPressed() {
        recorder.stop()
        context.goToRecordState(RecordStateStopped(context, recorder, tempFileProvider))
    }

    override fun onRestartPressed() {
        Log.d(TAG, "Paused -> Initial")
        reinitialiseRecorder()
        context.goToRecordState(RecordStateInitial(context, tempFileProvider))
    }
}