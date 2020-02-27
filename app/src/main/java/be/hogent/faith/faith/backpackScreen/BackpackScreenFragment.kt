package be.hogent.faith.faith.backpackScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentBackpackBinding
import be.hogent.faith.databinding.PanelAddItemBagpackBinding
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SaveEventDialog
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.storage.IDummyStorageRepository
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_enter_event_details.background_event_details
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.UUID

class BackpackScreenFragment : Fragment() {

    private var navigation: BackpackDetailsNavigationListener? = null

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
 //   private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var backpackBinding: be.hogent.faith.databinding.FragmentBackpackBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backpackBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_backpack, container, false)

        backpackBinding.backpackViewModel = backpackViewModel
        backpackBinding.lifecycleOwner = this@BackpackScreenFragment
        return backpackBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BackpackDetailsNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }

    private fun updateUI() {

        backpackBinding.recyclerviewBackpack.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Start with empty list and then fill it in

            adapter = DetailThumbnailsAdapter(
                emptyList(),
                requireNotNull(activity) as BackpackScreenActivity
            )
        }

        detailThumbnailsAdapter =
            backpackBinding.recyclerviewBackpack.adapter as DetailThumbnailsAdapter
        // determineRVVisibility()
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    private fun startListeners() {
        // TODO Update adapter when backpack changes, change this to userviewmodel when working with real data

        backpackViewModel.details.observe(this, Observer { details ->
            detailThumbnailsAdapter?.updateDetailsList(details)
        })

        backpackBinding.btnAdd.setOnClickListener {
            onAddClicked()
            rotateBtnForward(backpackBinding.btnAdd)
        }
    }

    private fun rotateBtnForward(view: View) {
        ViewCompat.animate(view)
            .rotation(135.0F)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start()
    }

    private fun rotateBtnBackwards(view: View) {
        ViewCompat.animate(view)
            .rotation(0.0F)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start()
    }

    private fun onAddClicked() {
        val popUpMenu = PopupMenu(activity, backpackBinding.btnAdd)
        popUpMenu.menuInflater.inflate(R.menu.menu_backpack, popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.backpack_menu_addAudio ->
                  navigation?.startAudioDetailFragment()
                R.id.backpack_menu_addDrawing ->
                    navigation?.startDrawingDetailFragment()
                R.id.backpack_menu_addFile ->
                    navigation?.startFileDetailFragment()
                R.id.backpack_menu_addFoto ->
                    navigation?.startPhotoDetailFragment()
                R.id.backpack_menu_addText ->
                   navigation?.startTextDetailFragment()
                R.id.backpack_menu_addVideo ->
                   navigation?.startVideoDetailFragment()
            }
            true
        }
        popUpMenu.show()
    }

    interface BackpackDetailsNavigationListener {
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()
        fun startVideoDetailFragment()
        fun startFileDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeBackpack()
    }

    companion object {
        fun newInstance(): BackpackScreenFragment {
            return BackpackScreenFragment()
        }
    }


}












