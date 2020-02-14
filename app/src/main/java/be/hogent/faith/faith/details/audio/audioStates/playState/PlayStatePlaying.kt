package be.hogent.faith.faith.details.audio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.mediaplayer.MediaPlayerAdapter
import timber.log.Timber

class PlayStatePlaying(
    context: AudioContext,
    override val mediaPlayer: MediaPlayerAdapter,
    override val recorder: MediaRecorder
) : PlayState(context) {

    companion object {
        /**
         * Get a new [PlayStatePlaying] with the [MediaPlayer] already playing
         */
        fun getPlayingState(
            context: AudioContext,
            mediaPlayer: MediaPlayerAdapter,
            recorder: MediaRecorder
        ): PlayStatePlaying {
            return PlayStatePlaying(context, mediaPlayer, recorder).apply {
                // Go to Initialized state
                initialisePlayer()
                mediaPlayer.play()
            }
        }
    }

    init {
        Timber.d("Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
    }

    override fun onPlayPressed() {
        Timber.d("Playing -> Playing: was already playing")
    }

    override fun onPausePressed() {
        Timber.d("Playing -> Paused")
        mediaPlayer.pause()
        context.goToNextState(PlayStatePaused(context, mediaPlayer, recorder))
    }

    override fun onStopPressed() {
        Timber.d("Playing -> Stopped")
        mediaPlayer.reset()
        context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Timber.d("Playing -> Playing: can't start a recording when a recording is playing")
    }
}