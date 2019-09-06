package be.hogent.faith.faith.emotionCapture.recordAudio

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentRecordAudioBinding
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

const val REQUESTCODE_AUDIO = 12

class RecordAudioFragment : Fragment() {
    private val eventViewModel: EventViewModel by sharedViewModel()

    private val recordAudioViewModel: RecordAudioViewModel by viewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var hasAudioRecordingPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recordAudioViewModel.pauseSupported.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recordAudioBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_record_audio, container, false)
        recordAudioBinding.recordAudioViewModel = recordAudioViewModel
        recordAudioBinding.eventViewModel = eventViewModel
        recordAudioBinding.lifecycleOwner = this

        return recordAudioBinding.root
    }

    override fun onStart() {
        super.onStart()
        checkAudioRecordingPermission()
        startListeners()
    }

    override fun onStop() {
        super.onStop()
        // TODO: make sure both the mediarecorder and audioplayer are released
        recordAudioViewModel.audioState.value?.release()
    }

    private fun hasRecordingPermissions(): Boolean {
        return checkSelfPermission(
            activity!!,
            Manifest.permission.RECORD_AUDIO
        ) == PERMISSION_GRANTED
    }

    /**
     * Checks for audio permissions and initialises the [RecordAudioViewModel] fully once the
     * required permissions are given.
     */
    private fun checkAudioRecordingPermission() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            recordAudioViewModel.initialiseState()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUESTCODE_AUDIO) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                hasAudioRecordingPermission = true
                checkAudioRecordingPermission()
                recordAudioViewModel.initialiseState()
            } else {
                Toast.makeText(
                    this.context,
                    getString(R.string.permission_record_audio),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startListeners() {
        eventViewModel.recordingSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_audio_success, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        fun newInstance(): RecordAudioFragment {
            return RecordAudioFragment()
        }
    }
}
