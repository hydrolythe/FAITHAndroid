package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStateRecording(
    context: RecordingContext,
    recorder: MediaRecorder,
    tempFileProvider: TempFileProvider
) : RecordState(context, recorder, tempFileProvider) {

    override fun onRecordPressed() {
        Log.d(TAG, "Recording -> Recording: recorder was already recording, recording again does nothing")
    }

    override fun onPausePressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.pause()
            context.goToRecordState(RecordStatePaused(context, recorder, tempFileProvider))
            Log.d(TAG, "Recording -> Paused")
        } else {
            Log.d(TAG, "Recording -> Recording: pausing the recorder is not supported on a device with this API level")
        }
    }

    override fun onStopPressed() {
        recorder.stop()
        context.goToRecordState(RecordStateStopped(context, recorder, tempFileProvider))
        Log.d(TAG, "Recording -> Stopped")
    }

    override fun onRestartPressed() {
        reinitialiseRecorder()
        context.goToRecordState(RecordStateInitial(context, tempFileProvider))
        Log.d(TAG, "Recording -> Initial")
    }
}