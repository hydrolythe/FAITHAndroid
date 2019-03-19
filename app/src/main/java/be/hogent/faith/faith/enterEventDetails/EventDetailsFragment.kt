package be.hogent.faith.faith.enterEventDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEnterEventDetailsBinding
import be.hogent.faith.faith.UserViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventDetailsViewModel: EventDetailsViewModel
    private lateinit var eventDetailsBinding: FragmentEnterEventDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = getViewModel<UserViewModel>()
        eventDetailsViewModel = getViewModel { parametersOf(userViewModel.user, arguments?.getSerializable(ARG_EVENTUUID)) }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_event_details, container, false)
        eventDetailsBinding.eventDetailsVM = eventDetailsViewModel
        eventDetailsBinding.lifecycleOwner = this
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
    }

    private fun startListeners() {
        eventDetailsViewModel.eventTitle.observe(this, Observer { newTitle ->
            eventDetailsViewModel.event.value?.let { event ->
                event.title = newTitle
            }
        })
        eventDetailsViewModel.emotionAvatarClicked.observe(this, Observer {
            navigation?.startDrawFragment()
        })
    }

    interface EventDetailsNavigationListener {
        fun startDrawFragment()
    }

    companion object {
        fun newInstance(eventUuid: UUID?): EventDetailsFragment {
            return EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_EVENTUUID, eventUuid)
                }
            }
        }
    }
}