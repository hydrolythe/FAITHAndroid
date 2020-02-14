package be.hogent.faith.faith.details.audio

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
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.audio.mediaplayer.PlaybackInfoListener
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

const val REQUESTCODE_AUDIO = 12
private const val AUDIO_DETAIL = "An existing AudioDetail"

class RecordAudioFragment : Fragment(), DetailFragment<AudioDetail>, PlaybackInfoListener {

    override lateinit var detailFinishedListener: DetailFinishedListener

    private val audioDetailViewModel: AudioDetailViewModel by viewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var navigation: AudioScreenNavigation? = null

    private var hasAudioRecordingPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (existingTextGiven()) {
            loadExistingAudioDetail()
        }

        audioDetailViewModel.pauseSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private fun loadExistingAudioDetail() {
        val existingDetail = arguments?.getSerializable(AUDIO_DETAIL) as AudioDetail
        audioDetailViewModel.loadExistingDetail(existingDetail)
    }

    private fun existingTextGiven(): Boolean {
        return arguments?.getSerializable(AUDIO_DETAIL) != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recordAudioBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_record_audio, container, false)
        recordAudioBinding.audioDetailViewModel = audioDetailViewModel
        recordAudioBinding.lifecycleOwner = this

        return recordAudioBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AudioScreenNavigation) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
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
     * Checks for audio permissions and initialises the [AudioDetailViewModel] fully once the
     * required permissions are given.
     */
    private fun checkAudioRecordingPermission() {
        if (!hasRecordingPermissions()) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUESTCODE_AUDIO
            )
        } else {
            audioDetailViewModel.initialiseState()
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
                audioDetailViewModel.initialiseState()
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
        audioDetailViewModel.savedDetail.observe(this, Observer { finishedDetail ->
            Toast.makeText(context, R.string.save_audio_success, Toast.LENGTH_SHORT).show()

            detailFinishedListener.onDetailFinished(finishedDetail)

            navigation?.backToEvent()
        })
        audioDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        fun newInstance(): RecordAudioFragment {
            return RecordAudioFragment()
        }

        fun newInstance(existingDetail: AudioDetail): RecordAudioFragment {
            val newInstance = RecordAudioFragment()
            newInstance.arguments = Bundle().apply {
                putSerializable(AUDIO_DETAIL, existingDetail)
            }
            return newInstance
        }
    }

    interface AudioScreenNavigation {
        fun backToEvent()
    }

    override fun onLogUpdated(formattedMessage: String?) {
        Timber.i(formattedMessage)
    }

    override fun onDurationChanged(duration: Int) {
    }

    override fun onPositionChanged(position: Int) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaybackCompleted() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
