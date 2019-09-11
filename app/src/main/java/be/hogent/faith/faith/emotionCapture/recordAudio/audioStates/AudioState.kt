package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AudioState(
    internal val context: AudioContext
) : KoinComponent {

    protected val tempFileProvider: TempFileProvider by inject()

    protected abstract val mediaPlayer: MediaPlayer
    protected abstract val recorder: MediaRecorder

    abstract fun onPlayPressed()
    abstract fun onPausePressed()
    abstract fun onStopPressed()
    abstract fun onRecordPressed()

    abstract val playButtonEnabled: Boolean
    abstract val pauseButtonEnabled: Boolean
    abstract val stopButtonEnabled: Boolean
    abstract val recordButtonEnabled: Boolean

    /**
     * Resets the internal [MediaRecorder] and [MediaPlayer]
     */
    fun reset() {
        mediaPlayer.reset()
        recorder.reset()
    }

    /**
     * Releases the internal [MediaRecorder] and [MediaPlayer]
     */
    fun release() {
        mediaPlayer.release()
        recorder.release()
    }
}
