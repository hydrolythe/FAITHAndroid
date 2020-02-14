package be.hogent.faith.faith.details.audio.audioStates.recordState

import android.media.MediaRecorder
import be.hogent.faith.faith.details.audio.AudioViewState
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.mediaplayer.MediaPlayerHolder
import timber.log.Timber

class RecordStateInitial(context: AudioContext) : RecordState(context) {

    override val mediaPlayer = MediaPlayerHolder()
    override val recorder: MediaRecorder = MediaRecorder()

    override val audioViewState = AudioViewState.Initial

    init {
        initializeRecorder()
    }

    override fun onPlayPressed() {
        Timber.d("Initial -> Initial: nothing has been recorded yet")
    }

    override fun onRecordPressed() {
        Timber.d("Initial -> Recording")
        recorder.start()
        context.goToNextState(RecordStateRecording(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Timber.d("Initial -> Initial: Can't pause a recording that hasn't started yet.")
    }

    override fun onStopPressed() {
        Timber.d("Initial -> Initial: can't stop a recording that hasn't started yet.")
    }
}