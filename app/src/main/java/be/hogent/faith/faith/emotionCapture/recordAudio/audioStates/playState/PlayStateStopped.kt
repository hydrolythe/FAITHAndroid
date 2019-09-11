package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState.RecordStateRecording
import be.hogent.faith.util.TAG

class PlayStateStopped(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : PlayState(context) {

    override val playButtonEnabled = true
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = false
    override val recordButtonEnabled = true

    override fun onPlayPressed() {
        // Go to Prepared state
        mediaPlayer.prepare()
        // Go to Started state
        mediaPlayer.start()
        context.goToNextState(PlayStatePlaying(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Stopped -> Stopped: can't pause when stopped")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Stopped -> Stopped: was already stopped")
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Stopped -> Recording. Resetting the Mediaplayer")
        // Resetting is required so we can start the initialisation process from scratch.
        mediaPlayer.reset()
        context.goToNextState(
            RecordStateRecording.getRecordingState(
                context,
                mediaPlayer,
                recorder
            )
        )
    }
}