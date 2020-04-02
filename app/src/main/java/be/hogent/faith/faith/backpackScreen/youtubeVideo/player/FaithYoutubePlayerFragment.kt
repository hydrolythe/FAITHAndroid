package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Uses: https://github.com/PierfrancescoSoffritti/android-youtube-player
 * Why? - Customisable lay-out if we use a video player screen
 *      - Ability to disable YouTube buttons
 *
 * Read more: https://medium.com/@soffritti.pierfrancesco/how-to-play-youtube-videos-in-your-android-app-c40427215230
 */

/**
 * Abstract FaithYoutubePlayer fragment
 * should be extended by every fragment where this player is needed
 * Communicates with the FaithYoutubePlayerViewModel that contains the player and its current state
 */

abstract class FaithYoutubePlayerFragment() : IVideoPlayer, Fragment() {

    private val playerViewModel : FaithYoutubePlayerViewModel by viewModel()
    private var faithPlayerListener: FaithYoutubePlayerListener? = null

    /**
     * Attach listeners to all the elements of the player if they are present
     */
    override fun onStart() {
        super.onStart()

        playerViewModel.player.observe(this, Observer {
            if(faithPlayerListener == null && it != null){
                /**
                 * Creates a listener from our player with the new track to play
                 */
                faithPlayerListener =
                    FaithYoutubePlayerListener(
                        it,
                        it.youtubePlayerView.inflateCustomPlayerUi(R.layout.player_youtube_custom)
                    )

                it.youtubePlayerView.addYouTubePlayerListener(faithPlayerListener!!)

                lifecycle.addObserver(it.youtubePlayerView)

                playerViewModel.player.value!!.playButton.setOnClickListener {
                    playerViewModel.onPlayClicked()
                }

                playerViewModel.player.value!!.pauseButton.setOnClickListener {
                    playerViewModel.onPauseClicked()
                }

                if(playerViewModel.player.value!!.hasStopButton()){
                    playerViewModel.player.value!!.stopButton!!.setOnClickListener {
                        playerViewModel.onStopClicked()
                    }
                }

                playerViewModel.player.value!!.fullscreenButton!!.setOnClickListener {
                    setFullScreen()
                }

                if(playerViewModel.player.value!!.hasSeekbar()){
                    playerViewModel.player.value!!.seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            if (fromUser) {
                                seekTo(playerViewModel.currentState.value!!, progress.toFloat())
                            }
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    })
                }
            }
        })

        playerViewModel.currentState.observe(this, Observer {
            if (it == VideoPlayerState.PLAYING)
                playVideo(it)
            if (it == VideoPlayerState.PAUSED)
                pauseVideo(it)
            if (it == VideoPlayerState.STOPPED)
                stopVideo(it)
        })
    }

    fun setFaithYoutubePlayer(faithYoutubePlayer: FaithYoutubePlayer){
        playerViewModel.setPlayer(faithYoutubePlayer)
    }

    override fun playVideo(currentState: VideoPlayerState) {
       faithPlayerListener!!.playVideo(currentState)
    }

    override fun pauseVideo(currentState: VideoPlayerState) {
        faithPlayerListener!!.pauseVideo(currentState)
    }

    override fun stopVideo(currentState: VideoPlayerState) {
       faithPlayerListener!!.stopVideo(currentState)
    }

    override fun seekTo(currentState: VideoPlayerState, time: Float) {
       faithPlayerListener!!.seekTo(currentState, time)
       playerViewModel.player.value!!.currentTimeField!!.text =
           createTimeLabel(
               time
           )
    }

    override fun setFullScreen() {
        if(playerViewModel.player.value!!.isFullscreen) {
            onYouTubePlayerExitFullScreen()
        }
        else{
            playerViewModel.player.value!!.youtubePlayerView.enterFullScreen()
            onYouTubePlayerEnterFullScreen()
        }
        playerViewModel.player.value!!.isFullscreen = !playerViewModel.player.value!!.isFullscreen
    }

    open fun onYouTubePlayerEnterFullScreen(){
        val viewParams: ViewGroup.LayoutParams = playerViewModel.player.value!!.playerParentView.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerViewModel.player.value!!.playerParentView.layoutParams = viewParams
    }

    open fun onYouTubePlayerExitFullScreen(){
        val viewParams: ViewGroup.LayoutParams = playerViewModel.player.value!!.playerParentView.layoutParams
        viewParams.height = resources.getDimension(R.dimen.match_constraint).toInt()
        viewParams.width = resources.getDimension(R.dimen.match_constraint).toInt()
        playerViewModel.player.value!!.playerParentView.layoutParams = viewParams
    }

    /**
     * Behaviour required by Play Store. The audio must be stopped when the video isn't visible.
     */
    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
    }

    fun resetPlayer(){
        faithPlayerListener = null
        playerViewModel.resetPlayer()
    }

    private fun stopPlayer() {
        playerViewModel.player.value!!.youtubePlayerView.release()
    }
}