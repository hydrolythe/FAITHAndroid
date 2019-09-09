package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

class PlayStatePlaying(context: AudioContext) : PlayState(context) {

    companion object {
        /**
         * Get a new [PlayStatePlaying] with the [MediaPlayer] already playing
         */
        fun getPlayingState(context: AudioContext): PlayStatePlaying {
            return PlayStatePlaying(context).apply {
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
        context.goToNextState(PlayStatePaused(context))
    }

    override fun onStopPressed() {
        Log.d(TAG, "Playing -> Stopped")
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Playing -> Playing: can't start a recording when a recording is playing")
    }
}