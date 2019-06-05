package be.hogent.faith.faith.emotionCapture.recordAudio

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
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
            context.setState(RecordStatePaused())
            Log.d(TAG, "Recorder is now paused")
        } else {
            Log.d(TAG, "Pausing the recorder is not supported on a device with this API level")
        }
    }

    override fun onStopPressed() {
        recorder.stop()
        eventViewModel.saveAudio(tempFileProvider.tempAudioRecordingFile)
    }

    override fun onPlayPressed() {
        Log.d(TAG, "Recorder was recording, stop recording before pressing play")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRestartPressed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}