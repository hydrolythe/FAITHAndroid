package be.hogent.faith.faith.details.audio.audioStates

import be.hogent.faith.faith.details.audio.AudioViewState
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.view.View
import be.hogent.faith.faith.details.audio.mediaplayer.MediaPlayerAdapter
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AudioState(
    internal val context: AudioContext
) : KoinComponent {

    protected val tempFileProvider: TempFileProvider by inject()

    protected abstract val mediaPlayer: MediaPlayerAdapter
    protected abstract val recorder: MediaRecorder

    abstract fun onPlayPressed()
    abstract fun onPausePressed()
    abstract fun onStopPressed()
    abstract fun onRecordPressed()

    open val saveButtonEnabled: Boolean = false

    abstract val audioViewState: AudioViewState

    open val recordingTimeVisibility = View.INVISIBLE

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
