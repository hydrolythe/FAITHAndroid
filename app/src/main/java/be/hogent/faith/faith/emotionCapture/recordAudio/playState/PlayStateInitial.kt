package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG
import java.io.IOException

class PlayStateInitial(
    context: PlayContext,
    tempFileProvider: TempFileProvider
) : PlayState(context, MediaPlayer()) {

    init {
        // An uninitialised MediaPlayer was passed to the superclass.
        // Now we initialise it.
        with(mediaPlayer) {
            try {
                setDataSource(tempFileProvider.tempAudioRecordingFile.path)
                prepare()
                start()
                Log.d(TAG, "Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
            } catch (e: IOException) {
                Log.e(TAG, "Preparing audio playback failed")
            }
        }
    }

    override fun onPlayPressed() {
        mediaPlayer.start()
        context.goToPlayState(PlayStatePlaying(context, mediaPlayer))
    }

    override fun onPausePressed() {
        Log.d(TAG, "Initial -> Initial: Can't pause when nothing was playing yet.")
    }

    override fun onStopPressed() {
        Log.d(TAG, "Initial -> Initial: Can't stop when nothing was playing yet.")
    }
}