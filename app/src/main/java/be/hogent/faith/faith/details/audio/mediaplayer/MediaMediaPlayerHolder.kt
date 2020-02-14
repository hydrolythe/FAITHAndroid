package be.hogent.faith.faith.details.audio.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MediaMediaPlayerHolder(context: Context) :
    MediaPlayerAdapter {
    private val mContext: Context = context.applicationContext
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var audioFile: File
    private var mPlaybackInfoListener: PlaybackInfoListener? = null
    private var mExecutor: ScheduledExecutorService? = null
    private var mSeekbarPositionUpdateTask: Runnable? = null
    /**
     * Once the [MediaPlayer] is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the [MainActivity] the [MediaPlayer] is
     * released. Then in the onStart() of the [MainActivity] a new [MediaPlayer]
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    private fun initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setOnCompletionListener {
                stopUpdatingCallbackWithPosition(true)
                logToUI("MediaPlayer playback completed")
                if (mPlaybackInfoListener != null) {
                    mPlaybackInfoListener!!.onStateChanged(PlaybackInfoListener.State.COMPLETED)
                    mPlaybackInfoListener!!.onPlaybackCompleted()
                }
            }
            logToUI("mMediaPlayer = new MediaPlayer()")
        }
    }

    fun setPlaybackInfoListener(listener: PlaybackInfoListener?) {
        mPlaybackInfoListener = listener
    }

    // Implements PlaybackControl.
    override fun loadMedia(audioFile: File) {
        this.audioFile = audioFile
        initializeMediaPlayer()
        try {
            logToUI("load() {1. setDataSource}")
            mMediaPlayer!!.setDataSource(audioFile.path)
        } catch (e: Exception) {
            logToUI(e.toString())
        }
        try {
            logToUI("load() {2. prepare}")
            mMediaPlayer!!.prepare()
        } catch (e: Exception) {
            logToUI(e.toString())
        }
        initializeProgressCallback()
        logToUI("initializeProgressCallback()")
    }

    override fun release() {
        if (mMediaPlayer != null) {
            logToUI("release() and mMediaPlayer = null")
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override val isPlaying: Boolean
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else false

    override fun play() {
        if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying) {
            logToUI(
                String.format(
                    "playbackStart() %s",
                    mContext.resources.getResourceEntryName(audioFile)
                )
            )
            mMediaPlayer!!.start()
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.PLAYING)
            startUpdatingCallbackWithPosition()
        }
    }

    override fun reset() {
        if (mMediaPlayer != null) {
            logToUI("playbackReset()")
            mMediaPlayer!!.reset()
            loadMedia(audioFile)
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.RESET)
            stopUpdatingCallbackWithPosition(true)
        }
    }

    override fun pause() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.PAUSED)
            logToUI("playbackPause()")
        }
    }

    override fun seekTo(position: Int) {
        if (mMediaPlayer != null) {
            logToUI(String.format("seekTo() %d ms", position))
            mMediaPlayer!!.seekTo(position)
        }
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private fun startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor()
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        mExecutor!!.scheduleAtFixedRate(
            mSeekbarPositionUpdateTask,
            0,
            PLAYBACK_POSITION_REFRESH_INTERVAL_MS.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private fun stopUpdatingCallbackWithPosition(resetUIPlaybackPosition: Boolean) {
        if (mExecutor != null) {
            mExecutor!!.shutdownNow()
            mExecutor = null
            mSeekbarPositionUpdateTask = null
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onPositionChanged(0)
            }
        }
    }

    private fun updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            val currentPosition = mMediaPlayer!!.currentPosition
            mPlaybackInfoListener?.onPositionChanged(currentPosition)
        }
    }

    override fun initializeProgressCallback() {
        val duration = mMediaPlayer!!.duration
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener!!.onDurationChanged(duration)
            mPlaybackInfoListener!!.onPositionChanged(0)
            logToUI(
                String.format(
                    "firing setPlaybackDuration(%d sec)",
                    TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
                )
            )
            logToUI("firing setPlaybackPosition(0)")
        }
    }

    private fun logToUI(message: String) {
        mPlaybackInfoListener?.onLogUpdated(message)
    }

    companion object {
        const val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000
    }
}
