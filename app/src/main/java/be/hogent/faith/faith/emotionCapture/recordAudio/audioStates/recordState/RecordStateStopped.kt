package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState.PlayStatePlaying
import be.hogent.faith.util.TAG

class RecordStateStopped(
    context: AudioContext,
    override val mediaPlayer: MediaPlayer,
    override val recorder: MediaRecorder
) : RecordState(context) {

    override val playButtonEnabled = true
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = false
    override val recordButtonEnabled = true

    override fun onPlayPressed() {
        Log.d(TAG, "RecordStopped -> PlayStatePlaying")
        context.goToNextState(PlayStatePlaying.getPlayingState(context, mediaPlayer, recorder))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Stopped->Recording")
        recorder.reset()
        context.goToNextState(
            RecordStateRecording.getRecordingState(
                context,
                mediaPlayer,
                recorder
            )
        )
    }

    override fun onPausePressed() {
        Log.d(TAG, "Stopped->Stopped: Can't stop a paused recording")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Stopped->Stopped: Recorder was already stopped")
    }
}