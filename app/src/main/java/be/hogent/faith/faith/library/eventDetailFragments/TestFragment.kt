package be.hogent.faith.faith.library.eventDetailFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.hogent.faith.R

class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        return rootView
    }

    companion object {

        fun newInstance(): TestFragment {
            return TestFragment()
        }
    }
}