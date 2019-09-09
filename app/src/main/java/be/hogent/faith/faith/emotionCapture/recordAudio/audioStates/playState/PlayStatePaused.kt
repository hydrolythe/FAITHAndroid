package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

class PlayStatePaused(context: AudioContext) : PlayState(context) {

    override val playButtonVisible: Int
        get() = View.VISIBLE
    override val pauseButtonVisible: Int
        get() = View.INVISIBLE
    override val stopButtonVisible: Int
        get() = View.VISIBLE
    override val recordButtonVisible: Int
        get() = View.INVISIBLE

    override fun onPlayPressed() {
        Log.d(TAG, "Paused -> Playing")
        mediaPlayer.start()
        context.goToNextState(PlayStatePlaying(context))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Paused -> Paused: was already paused")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Paused -> Stopped")
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Paused -> Paused: stop the playback to start a new recording")
    }
}