package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.util.TAG
import timber.log.Timber

/**
 * State used for playing audio from an existing detail.
 * When starting from scratch this state is skipped.
 */
class PlayStateInitial(
    context: AudioContext,
    private val audioDetail: AudioDetail
) : AudioState(context) {

    override val mediaPlayer: MediaPlayer = MediaPlayer()
    override val recorder: MediaRecorder = MediaRecorder()

    override val playButtonEnabled = true
    override val pauseButtonEnabled = false
    override val stopButtonEnabled = false
    override val recordButtonEnabled = false

    init {
        initialisePlayer()
    }

    /**
     * Initialise the [MediaPlayer].
     * Should only be called the very first time the [MediaPlayer] is made, as this internally
     * puts the player in the _initialized_ state.
     * When a recording has been stopped the player is in the _prepared_ state.
     * Going from _initialized_ to _prepared_ requires a call to _prepare()_.
     * You can only start playing once the [MediaPlayer] is prepared.
     *
     * More info in the develop documentations.
     */
    private fun initialisePlayer() {
        with(mediaPlayer) {
            try {
                setDataSource(audioDetail.file.path)
                prepare()
                Timber.d("Audio playback prepared")
            } catch (e: IllegalStateException) {
                Timber.e(TAG, "Continuing with the prepare step")
            }
        }

        mediaPlayer.setOnCompletionListener {
            Timber.d("Playing -> Stopped: finished playback")
            // Go from PlaybackCompleted to Stopped
            mediaPlayer.stop()
            context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
        }
    }

    override fun onPlayPressed() {
        Timber.d("Initial -> Playing")
        mediaPlayer.start()
        context.goToNextState(PlayStatePlaying(context, mediaPlayer, recorder))
    }

    override fun onPausePressed() {
        Timber.d("Initial -> Initial: pressing pause does nothing")
    }

    override fun onStopPressed() {
        Timber.d("Initial -> Initial: pressing stop does nothing")
    }

    override fun onRecordPressed() {
        Timber.d("Initial -> Initial: pressing record does nothing")
    }
}
