package be.hogent.faith.faith.backpackScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
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
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
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

private const val ARG_EVENTUUID = "eventUUID"
class BackpackScreenFragment : Fragment() {

    private lateinit var backpackBinding: be.hogent.faith.databinding.FragmentBackpackBinding
    private val backpackViewModel : BackpackViewModel by viewModel()
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    companion object {
        fun newInstance() = BackpackScreenFragment()
    }

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
    }
}













