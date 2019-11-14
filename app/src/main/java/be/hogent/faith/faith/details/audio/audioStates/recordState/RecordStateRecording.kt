package be.hogent.faith.faith.details.audio.audioStates.recordState

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.view.View
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
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
    // True by default, but [AudioDetailVM] has final decision because it can know if pause is supported
    override val pauseButtonEnabled = true

    override val recordingTimeVisibility = View.VISIBLE

    override fun onPlayPressed() {
        Timber.d("Recording -> Recording: nothing to play yet")
    }

    override fun onRecordPressed() {
        Timber.d("Recording -> Recording: recorder was already recording")
    }

    @SuppressLint("NewApi") // Checked by AudioDetailVM
    override fun onPausePressed() {
        if (pauseButtonEnabled) {
            Timber.d("Recording -> Paused")
            recorder.pause()
            context.goToNextState(RecordStatePaused(context, mediaPlayer, recorder))
        } else {
            Timber.d("Recording -> Recording: pausing the recorder is not supported on this device")
        }
    }

    override fun onStopPressed() {
        Timber.d("Recording -> Stopped: Finished recording")
        recorder.stop()
        context.finishedRecording = true
        context.goToNextState(RecordStateStopped(context, mediaPlayer, recorder))
    }
}
