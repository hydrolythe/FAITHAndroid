package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStatePaused(
    context: PlayContext,
    mediaPlayer: MediaPlayer
) : PlayState(context, mediaPlayer) {

    override fun onPlayPressed() {
        mediaPlayer.start()
        context.goToPlayState(PlayStatePlaying(context, mediaPlayer))
        Log.d(TAG, "Paused -> Paused: was already paused")
    }

    override fun onPausePressed() {
        Log.d(TAG, "Paused -> Paused: was already paused")
    }

    override fun onStopPressed() {
        mediaPlayer.stop()
        context.goToPlayState(PlayStatePaused(context, mediaPlayer))
        Log.d(TAG, "Paused -> Stopped")
    }
}