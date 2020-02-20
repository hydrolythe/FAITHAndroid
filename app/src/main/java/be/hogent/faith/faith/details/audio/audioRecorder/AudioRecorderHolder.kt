package be.hogent.faith.faith.details.audio.audioRecorder

import android.annotation.TargetApi
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

// Inspired by https://medium.com/androiddevelopers/building-a-simple-audio-app-in-android-part-3-3-ead4a0e10673
class AudioRecorderHolder(private val outputFile: File) : AudioRecorderAdapter {

    private var isRecording = false

    private var recorder: MediaRecorder? = null
    var recordingInfoListener: RecordingInfoListener? = null

    private var mExecutor: ScheduledExecutorService? = null
    private var updateRecordingDurationTask: Runnable? = null
    private var recordingDuration: Long = 0

    private var isPaused = false

    private fun initialiseRecorder() {
        recorder = MediaRecorder()
        recorder!!.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            prepare()
        }
        initializeProgressCallback()
    }

    override fun release() {
        recorder?.release()
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RESET)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun record() {
        if (recorder == null) {
            initialiseRecorder()
        }
        if (isPaused) {
            recorder?.resume()
            isPaused = false
        } else {
            recorder!!.start()
        }
        isRecording = true
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        startUpdatingCallbackWithDuration()
    }

    override fun stop() {
        if (isRecording) {
            recorder?.stop()
            recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.STOPPED)
            stopUpdatingCallbackWithDuration()
        }
    }

    override fun reset() {
        recorder?.reset()
        recorder = null
        outputFile.delete()
        recordingDuration = 0L
        recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.RESET)
        stopUpdatingCallbackWithDuration()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun pause() {
        if (isRecording) {
            recorder?.pause()
            recordingInfoListener?.onStateChanged(RecordingInfoListener.RecordingState.PAUSED)
            stopUpdatingCallbackWithDuration()
            isPaused = true
        }
    }

    private fun initializeProgressCallback() {
        recordingInfoListener?.let {
            it.onStateChanged(RecordingInfoListener.RecordingState.RESET)
            it.onRecordingDurationChanged(0)
        }
    }

    private fun startUpdatingCallbackWithDuration() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor()
        }
        if (updateRecordingDurationTask == null) {
            updateRecordingDurationTask = Runnable { updateRecordingDurationTask() }
        }
        Timber.i("AudioRecorderHolder Started scheduling")
        mExecutor!!.scheduleAtFixedRate(
            updateRecordingDurationTask,
            1,
            1,
            TimeUnit.SECONDS
        )
    }

    private fun updateRecordingDurationTask() {
        try {
            recordingDuration += 1
            Timber.i("AudioRecorderHolder duration updated: $recordingDuration")
            recordingInfoListener?.onRecordingDurationChanged(recordingDuration)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun stopUpdatingCallbackWithDuration() {
        if (mExecutor != null) {
            Timber.i("AudioRecorderHolder stopped")
            mExecutor!!.shutdownNow()
            mExecutor = null
            updateRecordingDurationTask = null
        }
    }
}