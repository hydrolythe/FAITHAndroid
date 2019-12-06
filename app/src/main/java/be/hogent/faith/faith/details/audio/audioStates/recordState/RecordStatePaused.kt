package be.hogent.faith.faith.details.audio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.util.TAG
import timber.log.Timber

/**
 * A state representing a paused recording
 * @throws UnsupportedOperationException if the device does not support pausing (API level 23 and below)
 */
class RecordStatePaused(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : RecordState(context) {

    override val playButtonEnabled = false
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = true
    override val recordButtonEnabled = true

    override fun onRecordPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.resume()
            Timber.d("Paused -> Recording")
            context.goToNextState(RecordStateRecording(context, mediaPlayer, recorder))
        } else {
            throw UnsupportedOperationException(
                "Device does not support resuming! How did you even pause?!"
            )
        }
    }

    override fun onPlayPressed() {
        Timber.d("Paused -> Paused: start a recording again by pressing record, not play")
    }

    override fun onPausePressed() {
        Timber.d("Paused -> Paused Recorder was already paused, won't pause again")
    }

    override fun onStopPressed() {
        Timber.d(TAG, "Paused -> Stopped")
        recorder.stop()
        context.goToNextState(RecordStateStopped(context, mediaPlayer, recorder))
    }
}