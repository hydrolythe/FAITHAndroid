package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordingContext
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStateRecording(
    private val context: RecordingContext,
    private val recorder: MediaRecorder,
    private val mediaPlayer: MediaPlayer,
    private val tempFileProvider: TempFileProvider
) : RecordState {

    override fun onRecordPressed() {
        Log.d(TAG, "Recorder was already recording, recording again does nothing")
    }

    override fun onPausePressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.pause()
            context.setState(
                RecordStatePaused(
                    context,
                    recorder,
                    mediaPlayer,
                    tempFileProvider
                )
            )
            Log.d(TAG, "Recording -> Paused")
        } else {
            Log.d(TAG, "Pausing the recorder is not supported on a device with this API level")
        }
    }

    override fun onStopPressed() {
        recorder.stop()
        context.setState(
            RecordStateStopped(
                context,
                recorder,
                mediaPlayer,
                tempFileProvider
            )
        )
        Log.d(TAG, "Recording -> Stopped")
        // TODO: let context save to VM if it receives stopped state?
//        eventViewModel.saveAudio(tempFileProvider.tempAudioRecordingFile)
    }

    override fun onRestartPressed() {
        Log.d(TAG, "Recording -> Initial: Restarting a recording means going resetting it")
    }
}