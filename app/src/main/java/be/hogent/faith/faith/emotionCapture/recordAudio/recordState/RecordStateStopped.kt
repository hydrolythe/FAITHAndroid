package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordStateStopped(
    private val context: RecordingContext,
    private val recorder: MediaRecorder,
    private val tempFileProvider: TempFileProvider
) : RecordState {

    override fun onRecordPressed() {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(tempFileProvider.tempAudioRecordingFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
            start()
        }

        Log.d(TAG, "Stopped->Recording")
        context.goToState(
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
        recorder.stop()
        Log.d(TAG, "Recorder was stopped, restarting stays in initial state: stopped")
    }
}