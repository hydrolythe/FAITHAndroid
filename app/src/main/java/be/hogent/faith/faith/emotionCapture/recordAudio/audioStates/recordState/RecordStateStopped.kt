package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState.PlayStatePlaying
import be.hogent.faith.util.TAG

class RecordStateStopped(context: AudioContext) : RecordState(context) {

    override val playButtonVisible: Int
        get() = View.VISIBLE
    override val pauseButtonVisible: Int
        get() = View.INVISIBLE
    override val stopButtonVisible: Int
        get() = View.INVISIBLE
    override val recordButtonVisible: Int
        get() = View.VISIBLE

    override fun onPlayPressed() {
        Log.d(TAG, "RecordStopped -> PlayStatePlaying")
        context.goToNextState(PlayStatePlaying.getPlayingState(context))
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Stopped->Recording")
        recorder.reset()
        context.goToNextState(RecordStateRecording.getRecordingState(context))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Stopped->Stopped: Can't stop a paused recording")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Stopped->Stopped: Recorder was already stopped")
    }
}