package be.hogent.faith.faith.videoplayer

import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Used to control the lifecycle of the videoplayer, init the player and play a new track
 */
abstract class FaithVideoPlayerFragment : Fragment() {

    private val playerViewModel: VideoPlayerViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        playerViewModel.currentVideo.observe(this, Observer {
            if (playerViewModel.player.value != null)
                playerViewModel.playNewVideo(it, requireContext())
        })
    }

    fun setFaithPlayer(faithVideoPlayer: FaithVideoPlayer) {
        playerViewModel.setPlayer(faithVideoPlayer)
    }

    fun playNewVideo(detail: Detail) {
        playerViewModel.setCurrentVideo(detail)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (playerViewModel.player.value != null){
            playerViewModel.player.value!!.stopPlayer()
            playerViewModel.resetPlayer()
        }
    }
}
