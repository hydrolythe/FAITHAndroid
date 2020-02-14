package be.hogent.faith.faith.details.audio.audioRecorder

import android.media.MediaRecorder
import java.io.File

class AudioRecorderHolder(outputFile: File) : AudioRecorderAdapter {
    private val recorder = MediaRecorder()

    init {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
        }
    }

    override fun release() {
        recorder.release()
    }

    override fun record() {
        recorder.start()
    }

    override fun stop() {
        recorder.stop()
    }

    override fun reset() {
        recorder.reset()
    }
}