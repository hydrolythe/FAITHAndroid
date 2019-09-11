package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.util.TAG

abstract class PlayState(context: AudioContext) : AudioState(context) {

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
    fun initialisePlayer() {
        with(mediaPlayer) {
            try {
                setDataSource(tempFileProvider.tempAudioRecordingFile.path)
                Log.d(TAG, "Audio playback prepared")
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Continuing with the prepare step")
            }
        }

        mediaPlayer.setOnCompletionListener {
            Log.d(TAG, "Playing -> Stopped: finished playback")
            // Go from PlaybackCompleted to Stopped
            mediaPlayer.stop()
            context.goToNextState(PlayStateStopped(context, mediaPlayer, recorder))
        }
    }
}
