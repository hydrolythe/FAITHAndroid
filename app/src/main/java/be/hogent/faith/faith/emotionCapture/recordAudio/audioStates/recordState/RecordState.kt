package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaRecorder
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class RecordState(context: AudioContext) : AudioState(context), KoinComponent {

    protected val recorder: MediaRecorder by inject()
    private val tempFileProvider: TempFileProvider by inject()

    /**
     * @see MediaRecorder.release
     */
    override fun release() {
        recorder.release()
    }

    /**
     * Initialises a [MediaRecorder] from the Initial up until the Prepared state
     */
    fun initializeRecorder() {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(tempFileProvider.tempAudioRecordingFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
        }
    }
}
