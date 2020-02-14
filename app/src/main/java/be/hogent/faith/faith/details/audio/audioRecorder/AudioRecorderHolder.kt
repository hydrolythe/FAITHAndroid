package be.hogent.faith.faith.details.audio.audioRecorder

import android.media.MediaRecorder
import java.io.File

class AudioRecorderHolder(private val outputFile: File) : AudioRecorderAdapter {

    private var isRecording = false

    private var recorder: MediaRecorder? = null
    var recordingInfoListener: RecordingInfoListener? = null

    private fun initialiseRecorder() {
        recorder = MediaRecorder()
        recorder!!.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
        }
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RESET)
    }

    override fun release() {
        recorder?.release()
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RESET)
    }

    override fun record() {
        if (recorder == null) {
            initialiseRecorder()
            recorder?.start()
            isRecording = true
            recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        }
    }

    override fun stop() {
        if (isRecording) {
            recorder?.stop()
            recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.STOPPED)
        }
    }

    override fun reset() {
        recorder?.reset()
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RESET)
    }
}