package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

class RecordStateRecording(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : RecordState(context) {

    companion object {
        /**
         * Get a new RecordStateRecording in the Recording State
         */
        fun getRecordingState(
            context: AudioContext,
            mediaPlayer: MediaPlayer,
            recorder: MediaRecorder
        ): RecordStateRecording {
            return RecordStateRecording(context, mediaPlayer, recorder).apply {
                initializeRecorder()
                recorder.start()
            }
        }
    }

    override val playButtonEnabled = false
    override val stopButtonEnabled = true
    override val recordButtonEnabled = false
    override val pauseButtonEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    override fun onPlayPressed() {
        Log.d(TAG, "Recording -> Recording: nothing to play yet")
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Recording -> Recording: recorder was already recording")
    }

    override fun onPausePressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Recording -> Paused")
            recorder.pause()
            context.goToNextState(RecordStatePaused(context, mediaPlayer, recorder))
        } else {
            Log.d(
                TAG,
                "Recording -> Recording: pausing the recorder is not supported on this device"
            )
        }
    }

    override fun onStopPressed() {
        Log.d(TAG, "Recording -> Stopped")
        recorder.stop()
        context.goToNextState(RecordStateStopped(context, mediaPlayer, recorder))
    }
}
