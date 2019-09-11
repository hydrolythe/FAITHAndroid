package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

class RecordStateInitial(context: AudioContext) : RecordState(context) {

    override val mediaPlayer: MediaPlayer = MediaPlayer()
    override val recorder: MediaRecorder = MediaRecorder()

    override val playButtonEnabled = false
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = false
    override val recordButtonEnabled = true

    init {
        initializeRecorder()
    }

    override fun onPlayPressed() {
        Log.d(TAG, "Initial -> Initial: nothing has been recorded yet")
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Initial -> Recording")
        recorder.start()
        context.goToNextState(RecordStateRecording(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Initial -> Initial: Can't pause a recording that hasn't started yet.")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Initial -> Initial: can't stop a recording that hasn't started yet.")
    }
}