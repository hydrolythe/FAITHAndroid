package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStateInitial(
    private val context: PlayContext,
    private val mediaPlayer: MediaPlayer
) : PlayState {

    init {
        mediaPlayer.prepare()
    }

    override fun onPlayPressed() {
        mediaPlayer.start()
        context.goToState(PlayStatePlaying(context, mediaPlayer))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Initial -> Initial: Can't pause when nothing was playing yet.")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Initial -> Initial: Can't stop when nothing was playing yet.")

    }
}