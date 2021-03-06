package be.hogent.faith.faith.library.eventDetails

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
import be.hogent.faith.databinding.FragmentLibraryEventdetailsBinding
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import be.hogent.faith.faith.library.LibraryActivity
import be.hogent.faith.faith.loadImageIntoView
import be.hogent.faith.faith.models.detail.PhotoDetail
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File

/**
 * A fragment representing a single Event detail screen, showing
 * all the different [Detail] objects for an event.
 */
class EventDetailFragment : Fragment() {

    /**
     * ViewModel used to monitor the last selected event.
     */
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()
    private lateinit var binding: FragmentLibraryEventdetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_library_eventdetails,
            container,
            false
        )
        binding.eventDetailsViewModel = eventDetailsViewModel
        binding.lifecycleOwner = this
        setUpRecyclerView()
        setupListeners()
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewLibraryEventdetails.apply {
            binding.recyclerViewLibraryEventdetails.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                // Start with empty list and then fill it in
                adapter = DetailThumbnailsAdapter(requireNotNull(activity) as LibraryActivity)
            }
        }
    }

    private fun setupListeners() {
        eventDetailsViewModel.avatarImage.observe(viewLifecycleOwner, Observer { image: File? ->
            if (image != null)
                loadImageIntoView(requireContext(), image.path, binding.imgAvatar)
            else
                binding.imgAvatar.setImageDrawable(null)
        })

        eventDetailsViewModel.details.observe(viewLifecycleOwner, Observer { details:List<Detail> ->
            with(binding.recyclerViewLibraryEventdetails.adapter as DetailThumbnailsAdapter) {
                submitList(details)
                // Added to ensure thumbnails are loaded once files have been downloaded.
                notifyDataSetChanged()
            }
        })

        eventDetailsViewModel.cancelButtonClicked.observe(viewLifecycleOwner, Observer {
            requireActivity().onBackPressed()
        })
        eventDetailsViewModel.errorMessage.observe(
            viewLifecycleOwner,
            Observer { errorMessageResourceID ->
                Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
            })
    }

    companion object {
        fun newInstance(): EventDetailFragment {
            return EventDetailFragment()
        }
    }
}
