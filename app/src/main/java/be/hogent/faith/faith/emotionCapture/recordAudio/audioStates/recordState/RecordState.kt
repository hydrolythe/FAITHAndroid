package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState

import android.media.MediaRecorder
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState

abstract class RecordState(
    context: AudioContext
) : AudioState(context) {

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
