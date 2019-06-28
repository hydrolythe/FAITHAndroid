package be.hogent.faith.faith.emotionCapture.recordAudio.recordState

import android.media.MediaRecorder
import be.hogent.faith.faith.util.TempFileProvider

abstract class RecordState(
    internal val context: RecordingContext,
    internal val recorder: MediaRecorder,
    internal val tempFileProvider: TempFileProvider
) {
    abstract fun onRecordPressed()
    abstract fun onPausePressed()
    abstract fun onStopPressed()
    abstract fun onRestartPressed()

    /**
     * @see MediaRecorder.release
     */
    internal fun release() {
        recorder.release()
    }

    internal fun initialiseRecorder() {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(tempFileProvider.tempAudioRecordingFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
        }
    }

    internal fun reinitialiseRecorder() {
        recorder.reset()
        initialiseRecorder()
    }
}
