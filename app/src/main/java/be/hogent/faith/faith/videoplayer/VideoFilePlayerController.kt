package be.hogent.faith.faith.videoplayer

import android.net.Uri
import android.os.Handler
import android.widget.VideoView
import java.io.File

const val ONE_SECOND = 1_000
class VideoFilePlayerController(
    private val videoView: VideoView,
    source: File
) : IVideoPlayer {


    init {
        videoView.setVideoURI(Uri.fromFile(source))
        videoView.start()

        videoView.setOnCompletionListener {
            stopVideo()
        }
    }

    override fun getDuration(): Float {
        return videoView.duration.toFloat()
    }

    override fun getCurrentPosition() : Float{
        return videoView.currentPosition.toFloat() / ONE_SECOND
    }

    override fun playVideo() {
        videoView.start()
    }

    override fun resumeVideo() {
        videoView.resume()
    }

    override fun pauseVideo() {
        videoView.pause()
    }

    override fun stopVideo() {
        videoView.pause()
        videoView.seekTo(0)
    }

    override fun seekTo(time: Float) {
        videoView.seekTo(time.toInt() * ONE_SECOND)
    }

    override fun stopPlayer() {
        videoView.stopPlayback()
    }
}