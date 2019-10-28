package be.hogent.faith.faith.library.eventDetailFragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import be.hogent.faith.R

class ViewTextDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ViewTextDetailFragment()
    }

    private lateinit var viewModel: ViewTextDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_text_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ViewTextDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
