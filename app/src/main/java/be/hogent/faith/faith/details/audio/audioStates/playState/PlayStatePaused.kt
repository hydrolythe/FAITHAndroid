package be.hogent.faith.faith.details.audio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import timber.log.Timber

class PlayStatePaused(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : PlayState(context) {

    override fun onPlayPressed() {
        Timber.d("Paused -> Playing")
        mediaPlayer.start()
        context.goToNextState(PlayStatePlaying(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Timber.d("Paused -> Paused: was already paused")
    }

    override fun onStopPressed() {
        Timber.d("Paused -> Stopped")
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Timber.d("Paused -> Paused: stop the playback to start a new recording")
    }
}