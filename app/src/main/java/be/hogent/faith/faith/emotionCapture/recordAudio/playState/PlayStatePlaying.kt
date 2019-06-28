package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.util.TAG

class PlayStatePlaying(
    context: PlayContext,
    mediaPlayer: MediaPlayer
) : PlayState(context, mediaPlayer) {

    override fun onPlayPressed() {
        Log.d(TAG, "Playing -> Playing: was already playing")
    }

    override fun onPausePressed() {
        mediaPlayer.pause()
        context.goToPlayState(PlayStatePaused(context, mediaPlayer))
        Log.d(TAG, "Playing -> Paused")
    }

    override fun onStopPressed() {
        mediaPlayer.stop()
        context.goToPlayState(PlayStateStopped(context, mediaPlayer))
        Log.d(TAG, "Playing -> Stopped")
    }
}