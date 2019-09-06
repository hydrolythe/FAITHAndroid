package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG
import org.koin.core.KoinComponent

class RecordStateInitial(context: AudioContext) : RecordState(context), KoinComponent {

    override val playButtonVisible: Int
        get() = View.INVISIBLE
    override val pauseButtonVisible: Int
        get() = View.INVISIBLE
    override val stopButtonVisible: Int
        get() = View.INVISIBLE
    override val recordButtonVisible: Int
        get() = View.VISIBLE

    init {
        initializeRecorder()
    }

    override fun onPlayPressed() {
        Log.d(TAG, "Initial -> Initial: nothing has been recorded yet")
    }

    override fun onRecordPressed() {
        Log.d(TAG, "Initial -> Recording")
        recorder.start()
        context.goToNextState(RecordStateRecording(context))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Initial -> Initial: Can't pause a recording that hasn't started yet.")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Initial -> Initial: can't stop a recording that hasn't started yet.")
    }
}