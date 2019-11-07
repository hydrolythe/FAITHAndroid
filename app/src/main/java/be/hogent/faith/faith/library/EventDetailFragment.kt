package be.hogent.faith.faith.library

import android.os.Bundle
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
import be.hogent.faith.faith.details.text.create.EnterTextFragment
import be.hogent.faith.faith.library.eventDetailFragments.TestFragment
import be.hogent.faith.faith.library.eventDetailsList.SelectedItemViewModel
import org.koin.android.ext.android.getKoin

import timber.log.Timber

/**
 * A fragment representing a single Event detail screen, showing
 * all the different [Detail] objects for an event.
 */
class EventDetailFragment : Fragment() {

    /**
     * The viewpager showing fragments for the different details.
     */
    private lateinit var mPager: ViewPager2

    /**
     * The Shared UserViewModel to get the details.
     * TODO : this should be refactored by creating a new ViewModel managing the details via a repository
     */
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    /**
     * ViewModel used to monitor the last selected [Event].
     */
    private lateinit var selectedItemViewModel: SelectedItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_eventdetails, container, false)
        selectedItemViewModel =
            ViewModelProviders.of(activity!!).get(SelectedItemViewModel::class.java)
        mPager = rootView.findViewById(R.id.pager_eventdetails)
        setListeners()
        return rootView
    }

    /**
     * Sets up the observers. In this case it will observe the [selectedItemViewModel] and observe
     * changes of its value. Whenever a new value (i.e. another event has been clicked) is observed
     * it deletes the pageradapter and set a new one with the desired [Detail] objects of the [Event].
     */
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
            var fragment: Fragment
            if (details.isNotEmpty()) {
                when (details[position]) {
                    is DrawingDetail -> {
                        Timber.i("Drawing")
                        return TestFragment.newInstance()
                    }
                    is PhotoDetail -> {
                        Timber.i("Photo")
                        return TestFragment.newInstance()
                    }
                    is TextDetail -> {
                        val detail = details[position].uuid
                        Timber.i("De detail UUID is : $detail")
                        fragment = EnterTextFragment.newInstance(details[position].uuid)
                        Timber.i("Textdetail")
                        return fragment
                    }
                    is AudioDetail -> {
                        Timber.i("Audiodetail")
                        return TestFragment.newInstance()
                    }
                }
            }
            return TestFragment.newInstance()
        }
    }
}
