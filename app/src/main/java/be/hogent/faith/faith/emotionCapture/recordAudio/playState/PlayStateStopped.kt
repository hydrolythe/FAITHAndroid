package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStateStopped(
    private val context: PlayContext,
    private val mediaPlayer: MediaPlayer
) : PlayState {

    override fun onPlayPressed() {
        mediaPlayer.start()
        context.goToState(PlayStatePlaying(context, mediaPlayer))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Stopped -> Stopped: can't pause when stopped")

    }

    override fun onStopPressed() {
        Log.d(TAG, "Stopped -> Stopped: was already stopped")
    }
}