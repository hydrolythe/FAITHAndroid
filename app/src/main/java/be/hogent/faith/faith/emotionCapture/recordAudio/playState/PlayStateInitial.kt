package be.hogent.faith.faith.emotionCapture.recordAudio.playState

import android.media.MediaPlayer
import android.util.Log
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG
import java.io.IOException

class PlayStateInitial(
    context: PlayContext,
    private val tempFileProvider: TempFileProvider
) : PlayState(context, MediaPlayer()) {

    override fun onPlayPressed() {
        // An uninitialised MediaPlayer was passed to the superclass.
        // We can only initialise it once the recording has been saved to the recordingFile, otherwise
        // initialisation fails.
        with(mediaPlayer) {
            try {
                setDataSource(tempFileProvider.tempAudioRecordingFile.path)
                prepare()
                Log.d(TAG, "Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
            } catch (e: IOException) {
                Log.e(TAG, "Preparing audio playback failed")
                Log.e(TAG, "Reason: ${e.localizedMessage}")
            }
        }

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