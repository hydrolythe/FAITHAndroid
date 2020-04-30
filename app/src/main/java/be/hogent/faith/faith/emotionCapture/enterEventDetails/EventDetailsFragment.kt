package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_enter_event_details.background_event_details
import kotlinx.android.synthetic.main.fragment_enter_event_details.img_event_details_avatar_zittend
import kotlinx.android.synthetic.main.view_button_color_avatar.img_event_details_avatar_inkleuren
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null

    private val eventViewModel: EventViewModel by sharedViewModel()
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var eventDetailsBinding: be.hogent.faith.databinding.FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    private val avatarProvider: AvatarProvider by inject()

    private lateinit var saveDialog: SaveEventDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // When an UUID is given the [eventViewModel] should be updated to show the given event's state.
        arguments?.getSerializable(ARG_EVENTUUID)?.let {
            val event = userViewModel.user.value?.getEvent(it as UUID)
            requireNotNull(event) { "Could not find event with UUID $it." }
            eventViewModel.setEvent(event)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventDetailsBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_enter_event_details,
                container,
                false
            )
        eventDetailsBinding.eventViewModel = eventViewModel
        eventDetailsBinding.lifecycleOwner = this@EventDetailsFragment

        return eventDetailsBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventDetailsNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }

    private fun updateUI() {
        setBackgroundImage()

        eventDetailsBinding.recyclerViewEventDetailsDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Start with empty list and then fill it in
            adapter = DetailThumbnailsAdapter(
                emptyList(),
                requireNotNull(activity) as EmotionCaptureMainActivity
            )
        }
        detailThumbnailsAdapter =
            eventDetailsBinding.recyclerViewEventDetailsDetails.adapter as DetailThumbnailsAdapter
        determineRVVisibility()
    }

    private fun setBackgroundImage() {
        Glide.with(requireContext())
            .load(R.drawable.park_achtergrond)
            .into(background_event_details)
    }

    /**
     * Determines whether the Recyclerview showing the details is visible or not. Will set status
     * to gone when no items are shown, will set visible when at least one detail is present.
     */
    private fun determineRVVisibility() {
        if (detailThumbnailsAdapter!!.itemCount > 0) {
            eventDetailsBinding.recyclerViewEventDetailsDetails.visibility = View.VISIBLE
        } else {
            eventDetailsBinding.recyclerViewEventDetailsDetails.visibility = View.GONE
        }
    }

    private fun startListeners() {
        // Update adapter when event changes
        eventViewModel.event.observe(this, Observer { event ->
            detailThumbnailsAdapter?.updateDetailsList(event.details)
            // check whether there are detail in de adapter. If so, show the RV, of not leave hidden
            determineRVVisibility()
        })

        userViewModel.user.observe(this, Observer { user ->
            Glide.with(requireContext())
                .load(avatarProvider.getAvatarDrawableZitten(user.avatarName))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(img_event_details_avatar_zittend)
            Glide.with(requireContext())
                .load(avatarProvider.getAvatarDrawableGezicht(user.avatarName))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(img_event_details_avatar_inkleuren)
        })

        // Four main actions
        eventViewModel.emotionAvatarClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        eventViewModel.cameraButtonClicked.observe(this, Observer {
            // navigation?.startTakePhotoFragment()
            navigation?.startPhotoDetailFragment()
        })
        eventViewModel.audioButtonClicked.observe(this, Observer {
            // navigation?.startRecordAudioFragment()
            navigation?.startAudioDetailFragment()
        })
        eventViewModel.textButtonClicked.observe(this, Observer {
            // navigation?.startRecordAudioFragment()
            navigation?.startTextDetailFragment()
        })
        eventViewModel.drawingButtonClicked.observe(this, Observer {
            // navigation?.startMakeDrawingFragment()
            navigation?.startDrawingDetailFragment()
        })

        eventViewModel.sendButtonClicked.observe(this, Observer {
            saveDialog = SaveEventDialog.newInstance()
            saveDialog.show(requireActivity().supportFragmentManager, null)
        })

        userViewModel.eventSavedState.observe(this, Observer {
            it?.let {
                handleDataStateSavingEvent(it)
            }
        })
    }

    private fun handleDataStateSavingEvent(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                saveDialog.hideProgressBar()
                Toast.makeText(context, R.string.save_event_success, Toast.LENGTH_LONG).show()
                saveDialog.dismiss()
                userViewModel.eventSavedHandled()

                // Drawing scope can now be closed so a new DrawingVM will be created when another
                // drawing is made.
                runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
                    it.close()
                }

                navigation?.closeEvent()
                // Go back to main screen
            }
            ResourceState.LOADING -> {
                saveDialog.showProgressBar()
            }
            ResourceState.ERROR -> {
                saveDialog.hideProgressBar()
                Toast.makeText(context, resource.message!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    interface EventDetailsNavigationListener {
        fun startDrawEmotionAvatarFragment()
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeEvent()
    }

    companion object {
        fun newInstance(eventUuid: UUID? = null): EventDetailsFragment {
            return EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    eventUuid?.let {
                        putSerializable(ARG_EVENTUUID, it)
                    }
                }
            }
        }
    }
}