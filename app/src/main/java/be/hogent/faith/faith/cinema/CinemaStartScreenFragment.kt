package be.hogent.faith.faith.cinema

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaStartBinding
import be.hogent.faith.faith.library.eventList.EventListFragment

class CinemaStartScreenFragment : Fragment() {


    private lateinit var binding: FragmentCinemaStartBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cinema_start, container, false)
        binding.lifecycleOwner = this
        return binding.root

    }
    companion object {
        fun newInstance(): CinemaStartScreenFragment {
            return CinemaStartScreenFragment()
        }
    }

    interface CinemaNavigationListener {
        fun closeCinema()
    }

}
