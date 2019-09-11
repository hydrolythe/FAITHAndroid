package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

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

    override val playButtonVisible: Int
        get() = View.INVISIBLE
    override val pauseButtonVisible: Int
        get() = View.VISIBLE
    override val stopButtonVisible: Int
        get() = View.VISIBLE
    override val recordButtonVisible: Int
        get() = View.INVISIBLE

    init {
        Log.d(TAG, "Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
    }

    override fun onPlayPressed() {
        Log.d(TAG, "Playing -> Playing: was already playing")
    }

    override fun onPausePressed() {
        Log.d(TAG, "Playing -> Paused")
        mediaPlayer.pause()
        context.goToNextState(PlayStatePaused(context, mediaPlayer, recorder))
    }

    override fun onStopPressed() {
        Log.d(TAG, "Playing -> Stopped")
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Playing -> Playing: can't start a recording when a recording is playing")
    }
}