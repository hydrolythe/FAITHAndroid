package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewYoutubeVideoBinding
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


/**
 * Uses: https://github.com/PierfrancescoSoffritti/android-youtube-player
 * Why? - Customisable lay-out if we use a video player screen
 *      - Ability to disable YouTube buttons
*
* Read more: https://medium.com/@soffritti.pierfrancesco/how-to-play-youtube-videos-in-your-android-app-c40427215230
*/
class ViewYoutubeVideoFragment(private val youtubeVideoDetail: YoutubeVideoDetail) : Fragment() {

    private lateinit var viewYoutubeVideoBinding : FragmentViewYoutubeVideoBinding
    private lateinit var youTubePlayerView : YouTubePlayerView
    private var navigation: ViewYoutubeVideoNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewYoutubeVideoBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_view_youtube_video, container, false)

        return viewYoutubeVideoBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewYoutubeVideoNavigation) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        youTubePlayerView = viewYoutubeVideoBinding.youtubePlayerView

        lifecycle.addObserver(youTubePlayerView)

        val customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.player_youtube_custom)


        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val ytController = YoutubePlayerController(
                    customPlayerUi,
                    youTubePlayer,
                    youtubeVideoDetail
                )

                youTubePlayer.addListener(ytController)

                val videoId = youtubeVideoDetail.videoId
                youTubePlayer.loadVideo(videoId, 0F)
            }

            /**
             * When the video ends --> go back to backpack
             */
            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                if(state == PlayerConstants.PlayerState.ENDED)
                    navigation!!.backToEvent()

                super.onStateChange(youTubePlayer, state)
            }
        })
    }

    companion object {
        fun newInstance(youtubeVideoDetail: YoutubeVideoDetail): ViewYoutubeVideoFragment {
            return ViewYoutubeVideoFragment(youtubeVideoDetail)
        }
    }

    /**
     * Behaviour required by Play Store. The audio must be stopped when the video isn't visible.
     */
    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }

    interface ViewYoutubeVideoNavigation {
        fun backToEvent()
    }
}
