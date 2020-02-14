package be.hogent.faith.faith.details.audio.audioStates.playState

import android.media.MediaPlayer
import android.media.MediaRecorder
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.details.audio.AudioViewState
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.audioStates.AudioState
import be.hogent.faith.faith.details.audio.mediaplayer.MediaPlayerAdapter
import be.hogent.faith.faith.details.audio.mediaplayer.MediaPlayerHolder
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

    override val mediaPlayer: MediaPlayerAdapter = MediaPlayerHolder()
    override val recorder: MediaRecorder = MediaRecorder()

    override val audioViewState = AudioViewState.FinishedRecording

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
                mediaPlayer.loadMedia(audioDetail.file)
                Timber.d("Audio playback prepared")
            } catch (e: IllegalStateException) {
                Timber.e(TAG, "Continuing with the prepare step")
            }
        }
    }

    override fun onPlayPressed() {
        Timber.d("Initial -> Playing")
        mediaPlayer.play()
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
