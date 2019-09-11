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

    abstract val playButtonVisible: Int
    abstract val pauseButtonVisible: Int
    abstract val stopButtonVisible: Int
    abstract val recordButtonVisible: Int

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
