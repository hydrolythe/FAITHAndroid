package be.hogent.faith.faith.emotionCapture.recordAudio

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG
import java.io.IOException

class RecordStateStopped(
    private val context: RecordingContext,
    private val recorder: MediaRecorder,
    private val mediaPlayer: MediaPlayer,
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

        Log.d(TAG, "Going to Record State: Recording")
        context.setState(RecordStateRecording())
    }

    override fun onPausePressed() {
        Log.d(TAG, "Recorder was stopped, pausing does nothing")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Recorder was already stopped")
    }

    override fun onPlayPressed() {
        mediaPlayer.apply {
            try {
                setDataSource(tempFileProvider.tempAudioRecordingFile.path)
                prepare()
                start()
                Log.d(TAG, "Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
            } catch (e: IOException) {
                Log.e(TAG, "Preparing audio playback failed")
            }
        }

        Log.d(TAG, "Going to Record State: Playing")
        context.setState(RecordStatePlaying())
    }

    override fun onRestartPressed() {
        Log.d(TAG, "Recorder was stopped, restarting stays in initial state: stopped")
    }

}