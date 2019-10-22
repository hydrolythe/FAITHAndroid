package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import timber.log.Timber

class PlayStatePlaying(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : PlayState(context) {

    companion object {
        /**
         * Get a new [PlayStatePlaying] with the [MediaPlayer] already playing
         */
        fun getPlayingState(
            context: AudioContext,
            mediaPlayer: MediaPlayer,
            recorder: MediaRecorder
        ): PlayStatePlaying {
            return PlayStatePlaying(context, mediaPlayer, recorder).apply {
                // Go to Initialized state
                initialisePlayer()
                // Go to Prepared state
                mediaPlayer.prepare()
                // Go to Started state
                mediaPlayer.start()
            }
        }
    }

    override val playButtonEnabled = false
    override val pauseButtonEnabled = true
    override val stopButtonEnabled = true
    override val recordButtonEnabled = false

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
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Timber.d("Playing -> Playing: can't start a recording when a recording is playing")
    }
}