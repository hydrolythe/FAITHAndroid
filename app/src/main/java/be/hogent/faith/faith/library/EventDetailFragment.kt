package be.hogent.faith.faith.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventDetailFragments.TestFragment
import be.hogent.faith.faith.library.eventDetailFragments.TextDetailFragment
import be.hogent.faith.faith.library.eventDetailsList.SelectedItemViewModel
import org.koin.android.ext.android.getKoin


/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a [EventListActivity]
 * in two-pane mode (on tablets) or a [EventDetailActivity]
 * on handsets.
 */
class EventDetailFragment : Fragment() {

    private lateinit var mPager: ViewPager2

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var selectedItemViewModel: SelectedItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_eventdetails, container, false)
        selectedItemViewModel =
            ViewModelProviders.of(activity!!).get(SelectedItemViewModel::class.java)
        mPager = rootView.findViewById(R.id.pager_eventdetails)
        setListeners()
        return rootView
    }


    private fun setListeners() {
        selectedItemViewModel.selectedItem.observe(this, Observer {
            val pagerAdapter = ScreenSlidePagerAdapter(
                this,
                userViewModel.user.value!!.events[selectedItemViewModel.selectedItem.value!!].details
            )
            mPager.adapter = pagerAdapter
            mPager.invalidate()
        })

    }


    /**
     * PagerAdapter which will create the required fragments of the details.
     */
    private inner class ScreenSlidePagerAdapter(fa: Fragment, var details: List<Detail>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return details.size
        }

        override fun createFragment(position: Int): Fragment {
            if (!details.isEmpty()) {
                when (details[position]) {
                    is DrawingDetail -> {
                        Log.i("Tag", "Drawing")
                    }
                    is PhotoDetail -> {
                        Log.i("TAG", "Photo")
                    }
                    is TextDetail -> {
                        Log.i("Tag", "Textdetail")
                        val detail = details[position] as TextDetail
                        return TextDetailFragment.newInstance(detail.file.absolutePath)
                    }
                    is AudioDetail -> {
                        Log.i("TAG", "Audiodetail")
                    }
                }
            }
            return TestFragment.newInstance()
        }
    }
}
