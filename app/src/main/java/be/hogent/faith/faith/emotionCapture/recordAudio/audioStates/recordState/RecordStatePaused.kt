package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.os.Build
import android.util.Log
import android.view.View
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.util.TAG

/**
 * A state representing a paused recording
 * @throws UnsupportedOperationException if the device does not support pausing (API level 23 and below)
 */
class RecordStatePaused(context: AudioContext) : RecordState(context) {

    override val playButtonVisible: Int
        get() = View.INVISIBLE
    override val pauseButtonVisible: Int
        get() = View.INVISIBLE
    override val stopButtonVisible: Int
        get() = View.VISIBLE
    override val recordButtonVisible: Int
        get() = View.VISIBLE

    override fun onRecordPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.resume()
            Log.d(TAG, "Paused -> Recording")
            context.goToNextState(RecordStateRecording(context))
        } else {
            throw UnsupportedOperationException(
                "Device does not support resuming! How did you even pause?!"
            )
        }
    }

    override fun onPlayPressed() {
        Log.d(TAG, "Paused -> Paused: start a recording again by pressing record, not play")
    }

    override fun onPausePressed() {
        Log.d(TAG, "Paused -> Paused Recorder was already paused, won't pause again")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Paused -> Stopped")
        recorder.stop()
        context.goToNextState(RecordStateStopped(context))
    }
}