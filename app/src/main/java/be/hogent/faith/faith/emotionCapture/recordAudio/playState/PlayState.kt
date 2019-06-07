package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer

abstract class PlayState (
    internal val context: PlayContext,
    internal val mediaPlayer: MediaPlayer
){
    abstract fun onPlayPressed()
    abstract fun onPausePressed()
    abstract fun onStopPressed()
}
