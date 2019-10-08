package be.hogent.faith.faith.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.hogent.faith.R


/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a [EventListActivity]
 * in two-pane mode (on tablets) or a [EventDetailActivity]
 * on handsets.
 */
class EventDetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_eventdetails, container, false)


        return rootView
    }


}
