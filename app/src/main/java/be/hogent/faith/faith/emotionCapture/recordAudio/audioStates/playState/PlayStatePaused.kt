package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

class PlayStatePaused(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : PlayState(context) {

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
        context.goToNextState(PlayStatePlaying(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Paused -> Paused: was already paused")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Paused -> Stopped")
        mediaPlayer.stop()
        context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Paused -> Paused: stop the playback to start a new recording")
    }
}