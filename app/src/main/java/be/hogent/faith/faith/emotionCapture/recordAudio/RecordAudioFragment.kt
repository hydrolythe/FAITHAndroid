package be.hogent.faith.faith.emotionCapture.recordAudio

import android.Manifest
import android.content.Context
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
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.UUID

const val REQUESTCODE_AUDIO = 12
private const val AUDIO_DETAIL_UUID = "uuid of the audio file"

class RecordAudioFragment : Fragment() {
    private val eventViewModel: EventViewModel by sharedViewModel()

    private val audioViewModel: AudioViewModel by viewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var navigation: AudioScreenNavigation? = null

    private var hasAudioRecordingPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (existingTextGiven()) {
            loadExistingAudioDetail()
        }

        audioViewModel.pauseSupported.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private fun loadExistingAudioDetail() {
        val detailUuid = arguments?.getSerializable(AUDIO_DETAIL_UUID) as UUID
        val givenDetail = eventViewModel.event.value!!.details.find { it.uuid === detailUuid }
        if (givenDetail is AudioDetail) {
            audioViewModel.loadExistingAudioDetail(givenDetail)
        } else {
            throw IllegalArgumentException("Got a Detail that wasn't an AudioDetail!")
        }
    }

    private fun existingTextGiven(): Boolean {
        return arguments?.getSerializable(AUDIO_DETAIL_UUID) != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recordAudioBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_record_audio, container, false)
        recordAudioBinding.audioViewModel = audioViewModel
        recordAudioBinding.eventViewModel = eventViewModel
        recordAudioBinding.lifecycleOwner = this

        return recordAudioBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AudioScreenNavigation) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        checkAudioRecordingPermission()
        startListeners()
    }

    private fun hasRecordingPermissions(): Boolean {
        return checkSelfPermission(
            activity!!,
            Manifest.permission.RECORD_AUDIO
        ) == PERMISSION_GRANTED
    }

    /**
     * Checks for audio permissions and initialises the [AudioViewModel] fully once the
     * required permissions are given.
     */
    private fun checkAudioRecordingPermission() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            audioViewModel.initialiseState()
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
                audioViewModel.initialiseState()
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

            audioViewModel.audioState.value?.reset()

            navigation?.backToEvent()
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        fun newInstance(detailUuid: UUID? = null): RecordAudioFragment {
            val newInstance = RecordAudioFragment()
            if (detailUuid != null) {
                newInstance.arguments = Bundle().apply {
                    putSerializable(AUDIO_DETAIL_UUID, detailUuid)
                }
            }
            return newInstance
        }
    }

    interface AudioScreenNavigation {
        fun backToEvent()
    }
}
