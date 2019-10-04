package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import timber.log.Timber

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
        Timber.d("Recording -> Recording: nothing to play yet")
    }

    override fun onRecordPressed() {
        Timber.d("Recording -> Recording: recorder was already recording")
    }

    override fun onPausePressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Timber.d("Recording -> Paused")
            recorder.pause()
            context.goToNextState(RecordStatePaused(context, mediaPlayer, recorder))
        } else {
            Timber.d(
                "Recording -> Recording: pausing the recorder is not supported on this device"
            )
        }
    }

    override fun onStopPressed() {
        Timber.d("Recording -> Stopped: Finished recording")
        recorder.stop()
        context.finishedRecording = true
        context.goToNextState(RecordStateStopped(context, mediaPlayer, recorder))
    }
}
