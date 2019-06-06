package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStatePlaying(
    private val context: PlayContext,
    private val mediaPlayer: MediaPlayer
) : PlayState {

    override fun onPlayPressed() {
        Log.d(TAG, "Playing -> Playing: was already playing")

    }

    override fun onPausePressed() {
        mediaPlayer.pause()
        context.goToState(PlayStatePaused(context, mediaPlayer))
        Log.d(TAG, "Playing -> Paused")
    }

    override fun onStopPressed() {
        mediaPlayer.stop()
        context.goToState(PlayStatePaused(context, mediaPlayer))
        Log.d(TAG, "Paused -> Stopped")
    }
}