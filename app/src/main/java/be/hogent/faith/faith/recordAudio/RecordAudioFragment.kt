package be.hogent.faith.faith.recordAudio

import androidx.fragment.app.Fragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class RecordAudioFragment : Fragment() {

    private val recordAudioViewModel: RecordAudioViewModel by viewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    companion object {
        fun newInstance(): RecordAudioFragment {
            return RecordAudioFragment()
        }
    }
}
