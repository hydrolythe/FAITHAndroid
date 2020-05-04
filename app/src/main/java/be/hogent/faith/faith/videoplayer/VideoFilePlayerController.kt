package be.hogent.faith.faith.videoplayer

import android.net.Uri
import android.os.Handler
import android.widget.VideoView
import java.io.File

private const val ONE_SECOND = 1_000
class VideoFilePlayerController(
    private val faithVideoPlayer: FaithVideoPlayer,
    private val videoView: VideoView,
    source: File
) : IVideoPlayer {

    private var handler: Handler? = null

    private val updateCurrentPosition = object : Runnable {
        override fun run() {
            faithVideoPlayer.setCurrentTimeVideo(videoView.currentPosition.toFloat() / ONE_SECOND)
            handler!!.postDelayed(this, ONE_SECOND.toLong())
        }
    }

    init {
        videoView.setVideoURI(Uri.fromFile(source))
        videoView.start()

        handler = Handler()
        handler!!.post(updateCurrentPosition)

        videoView.setOnPreparedListener {
            faithVideoPlayer.setDurationVideo(videoView.duration.toFloat() / ONE_SECOND)
        }

        videoView.setOnCompletionListener {
            stopVideo()
        }
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
        handler!!.removeCallbacks(updateCurrentPosition)
    }
}