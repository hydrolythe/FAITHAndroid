package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStatePaused(
    private val context: PlayContext,
    private val mediaPlayer: MediaPlayer
) : PlayState {

    override fun onPlayPressed() {
        mediaPlayer.start()
        context.goToState(PlayStatePlaying(context, mediaPlayer))
        Log.d(TAG, "Paused -> Paused: was already paused")

    }

    override fun onPausePressed() {
        Log.d(TAG, "Paused -> Paused: was already paused")
    }

    override fun onStopPressed() {
        mediaPlayer.stop()
        context.goToState(PlayStatePaused(context, mediaPlayer))
        Log.d(TAG, "Paused -> Stopped")
    }
}