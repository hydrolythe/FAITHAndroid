package be.hogent.faith.faith.details.audio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.audioStates.playState.PlayStatePlaying
import timber.log.Timber

class RecordStateStopped(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : RecordState(context) {

    override val playButtonEnabled = true
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = false
    override val recordButtonEnabled = true

    init {
        context.finishedRecording = true
    }

    override fun onPlayPressed() {
        Timber.d("RecordStopped -> PlayStatePlaying")
        context.goToNextState(PlayStatePlaying.getPlayingState(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Timber.d("Stopped->Recording")
        recorder.reset()
        context.finishedRecording = false
        context.goToNextState(
            RecordStateRecording.getRecordingState(
                context,
                mediaPlayer,
                recorder
            )
        )
    }

    override fun onPausePressed() {
        Timber.d("Stopped->Stopped: Can't stop a paused recording")
    }

    override fun onStopPressed() {
        Timber.d("Stopped->Stopped: Recorder was already stopped")
    }
}