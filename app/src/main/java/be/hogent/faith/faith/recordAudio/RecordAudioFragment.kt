package be.hogent.faith.faith.recordAudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentRecordAudioBinding
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class RecordAudioFragment : Fragment() {

    private val recordAudioViewModel: RecordAudioViewModel by viewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recordAudioBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_audio, container, false)
        recordAudioBinding.apply {
            recordAudioViewModel = this@RecordAudioFragment.recordAudioViewModel
            lifecycleOwner = this@RecordAudioFragment

        }
        return recordAudioBinding.root
    }

    companion object {
        fun newInstance(): RecordAudioFragment {
            return RecordAudioFragment()
        }
    }
}
