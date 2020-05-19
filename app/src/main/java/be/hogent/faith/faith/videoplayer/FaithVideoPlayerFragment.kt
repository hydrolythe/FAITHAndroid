package be.hogent.faith.faith.videoplayer

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.Detail
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Used to control the lifecycle of the videoplayer, init the player and play a new track
 */
abstract class FaithVideoPlayerFragment : Fragment() {

    private val playerViewModel: CurrentVideoViewModel by viewModel()
    private var player: FaithVideoPlayer? = null

    override fun onStart() {
        super.onStart()
        playerViewModel.currentVideo.observe(this, Observer {
            player?.playNewVideo(it, requireContext())
        })
    }

    fun setFaithPlayer(faithVideoPlayer: FaithVideoPlayer) {
        player = faithVideoPlayer
    }

    fun playNewVideo(detail: Detail) {
        playerViewModel.setCurrentVideo(detail)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stopPlayer()
        player = null
    }
}
