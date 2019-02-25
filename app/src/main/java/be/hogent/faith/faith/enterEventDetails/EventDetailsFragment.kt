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
import org.koin.android.viewmodel.ext.android.viewModel

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null
    private val eventDetailsViewModel: EventDetailsViewModel by viewModel()
    private lateinit var eventDetailsBinding: FragmentEnterEventDetailsBinding

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
            navigation?.startDrawEmotionAvatarFragment()
        })
    }

    interface EventDetailsNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }

    companion object {
        fun newInstance(): EventDetailsFragment {
            return EventDetailsFragment()
        }
    }
}