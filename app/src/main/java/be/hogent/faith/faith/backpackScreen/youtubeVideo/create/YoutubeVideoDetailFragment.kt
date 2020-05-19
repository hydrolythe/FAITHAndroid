package be.hogent.faith.faith.backpackScreen.youtubeVideo.create

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCreateYoutubeVideoBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayer
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayerFragment
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.di.KoinModules
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_back_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_fullscreen_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_pause_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_play_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_save_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.btn_stop_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.card_youtube_player
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.layer_fullscreen_yt_player
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.menu_container_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.seekbar_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.text_currentime_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.text_duration_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.time_container_yt_video
import kotlinx.android.synthetic.main.fragment_view_youtube_video.view.youtube_player_view
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.Timer
import java.util.TimerTask
import kotlin.reflect.KClass

/**
 * A simple [Fragment] subclass.
 */
class YoutubeVideoDetailFragment : FaithYoutubePlayerFragment(),
    DetailFragment<YoutubeVideoDetail> {

    private val youtubeVideoDetailViewModel: YoutubeVideoDetailViewModel by viewModel()
    private var youtubeSnippetAdapter: YoutubeSnippetAdapter? = null
    private lateinit var youtubeVideoDetailBinding: FragmentCreateYoutubeVideoBinding
    private lateinit var popupWindow: PopupWindow
    private var popupview: View? = null
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    override lateinit var detailFinishedListener: DetailFinishedListener

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        youtubeVideoDetailViewModel.setDetailsMetaData(title, dateTime)
    }

    private var navigation: YoutubeVideoDetailScreenNavigation? = null
    private var timer: Timer? = null
    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    private var youtubePlayerListener: YouTubePlayerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        youtubeVideoDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_youtube_video,
            container,
            false
        )

        youtubeVideoDetailBinding.youtubeViewModel = youtubeVideoDetailViewModel

        return youtubeVideoDetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        startListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is YoutubeVideoDetailScreenNavigation) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        }
    }

    private fun updateUI() {
        youtubeSnippetAdapter = YoutubeSnippetAdapter(context, object : SnippetClickListener {
            override fun onSnippetClick(snippet: YoutubeVideoDetail) {
                /**
                 * Opens a preview screen only when no other preview screen is already opened
                 */
                if (youtubeVideoDetailViewModel.showPreview.value != ShowPreview.SHOW)
                    youtubeVideoDetailViewModel.setSelectedSnippet(snippet)
            }
        })

        val manager = GridLayoutManager(context, 4)
        youtubeVideoDetailBinding.rvYoutubeView.layoutManager = manager

        youtubeVideoDetailBinding.rvYoutubeView.adapter = youtubeSnippetAdapter
    }

    private fun startListeners() {
        youtubeVideoDetailViewModel.snippets.observe(this, Observer {
            youtubeSnippetAdapter!!.submitList(it)
        })

        youtubeVideoDetailBinding.btnSearchVideo.setOnClickListener {
            if (youtubeVideoDetailBinding.editTextSearchVideo.text.toString().isNotEmpty())
                youtubeVideoDetailViewModel.onSearch(youtubeVideoDetailBinding.editTextSearchVideo.text.toString())
            else
                youtubeVideoDetailViewModel.clearSnippetsList()
        }

        youtubeVideoDetailViewModel.selectedSnippet.observe(this, Observer {
            if (it != null) {
                youtubeVideoDetailViewModel.showPreview()
            } else
                youtubeVideoDetailViewModel.hidePreview()
        })

        youtubeVideoDetailViewModel.showPreview.observe(this, Observer {
            if (it == ShowPreview.SHOW) {
                showPreviewScreen()
                startYoutubePlayer()
            } else if (it == ShowPreview.HIDE) {
                hidePreviewScreen()
                stopYoutubePlayer()
                youtubeVideoDetailViewModel.clearSelectedSnippet()
            }
        })

        youtubeVideoDetailViewModel.getDetailMetaData.observe(this, Observer {
            popupWindow.dismiss()
            val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity()::class as KClass<FragmentActivity>,
                TextDetail::class as KClass<Detail>
            )
            if (saveDialog != null) {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(getParentFragmentManager(), null)
            }
        })

        youtubeVideoDetailViewModel.savedDetail.observe(this, Observer {
            detailFinishedListener.onDetailFinished(youtubeVideoDetailViewModel.savedDetail.value!!)
            navigation?.backToEvent()
        })

        /**
         * Based on Instagram search. Delay of 400ms to lower the amount of requests send to the YouTube Data API.
         * Delay starts when the user stops writing
         *
         * TODO request and usage monitoring: if too much --> use a button instead
         */
        youtubeVideoDetailBinding.editTextSearchVideo.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {
                if (timer != null) timer!!.cancel()
            }

            override fun beforeTextChanged(c: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(c: Editable?) {
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (c.toString().isNotEmpty())
                            youtubeVideoDetailViewModel.onSearch(c.toString())
                        else
                            youtubeVideoDetailViewModel.clearSnippetsList()
                    }
                }, 500)
            }
        })

        youtubeVideoDetailViewModel.backToBackpack.observe(this, Observer {
            navigation?.backToEvent()
        })
    }

    private fun hidePreviewScreen() {
        youtubeVideoDetailBinding.rvYoutubeView.visibility = View.VISIBLE
        youtubeVideoDetailBinding.editTextSearchVideo.isEnabled = true

        popupWindow.dismiss()
    }

    /**
     * Shows a popup window with the youtube player
     * A pop-up window because that way we don't use our list and current search state in the fragment
     */
    private fun showPreviewScreen() {

        youtubeVideoDetailBinding.rvYoutubeView.visibility = View.GONE
        youtubeVideoDetailBinding.editTextSearchVideo.isEnabled = false

        popupview = layoutInflater.inflate(R.layout.fragment_view_youtube_video, null, false)
        popupWindow = PopupWindow(
            popupview,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        /**
         * onBackPressed() dismisses popup window and doesn't destroy fragment by setting isOutsideTouchable and isFocusable
         */
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupview!!.btn_back_yt_video.setOnClickListener {
            popupWindow.dismiss()
        }

        popupview!!.btn_save_yt_video.visibility = View.VISIBLE

        popupview!!.btn_save_yt_video.setOnClickListener {
            youtubeVideoDetailViewModel.onSaveClicked()
        }

        popupWindow.setOnDismissListener {
            youtubeVideoDetailViewModel.hidePreview()
        }

        popupWindow.showAtLocation(popupview, Gravity.CENTER, 0, 0)
    }

    /**
     * Behaviour required by Play Store. The audio must be stopped when the video isn't visible.
     */
    private fun stopYoutubePlayer() {
        resetPlayer()
    }

    /**
     * Everything you need to play a new video in your fragment
     */
    private fun startYoutubePlayer() {
        val faithYoutubePlayer =
            FaithYoutubePlayer(
                youtubeVideoDetail = youtubeVideoDetailViewModel.selectedSnippet.value!!,
                youtubePlayerView = popupview!!.youtube_player_view,
                playerParentView = popupview!!.card_youtube_player,
                playButton = popupview!!.btn_play_yt_video as ImageButton,
                pauseButton = popupview!!.btn_pause_yt_video as ImageButton
            )

        faithYoutubePlayer.currentTimeField = popupview!!.text_currentime_yt_video as TextView
        faithYoutubePlayer.durationField = popupview!!.text_duration_yt_video as TextView
        faithYoutubePlayer.seekBar = popupview!!.seekbar_yt_video as SeekBar
        faithYoutubePlayer.stopButton = popupview!!.btn_stop_yt_video as ImageButton
        faithYoutubePlayer.fullscreenButton = popupview!!.btn_fullscreen_yt_video as ImageButton
        faithYoutubePlayer.menuContainer = popupview!!.menu_container_yt_video
        faithYoutubePlayer.timeContainer = popupview!!.time_container_yt_video

        setFaithYoutubePlayer(faithYoutubePlayer)
    }

    override fun onYouTubePlayerEnterFullScreen() {
        super.onYouTubePlayerEnterFullScreen()
        popupview!!.layer_fullscreen_yt_player.visibility = View.VISIBLE
    }

    override fun onYouTubePlayerExitFullScreen() {
        super.onYouTubePlayerExitFullScreen()
        popupview!!.layer_fullscreen_yt_player.visibility = View.GONE
    }

    companion object {
        fun newInstance(): YoutubeVideoDetailFragment {
            return YoutubeVideoDetailFragment()
        }
    }

    interface YoutubeVideoDetailScreenNavigation {
        fun backToEvent()
    }
}
