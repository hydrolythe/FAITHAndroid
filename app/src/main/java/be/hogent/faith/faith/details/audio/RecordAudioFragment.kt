package be.hogent.faith.faith.details.audio

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
import be.hogent.faith.faith.details.audio.audioPlayer.AudioPlayerAdapter
import be.hogent.faith.faith.details.audio.audioPlayer.AudioPlayerHolder
import be.hogent.faith.faith.details.audio.audioPlayer.PlaybackInfoListener
import be.hogent.faith.faith.details.audio.audioRecorder.AudioRecorderAdapter
import be.hogent.faith.faith.details.audio.audioRecorder.AudioRecorderHolder
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

const val REQUESTCODE_AUDIO = 12
private const val AUDIO_DETAIL = "An existing AudioDetail"

// TODO: might be cleaner to be split in a record and play fragment, now this does both
class RecordAudioFragment : Fragment(), DetailFragment<AudioDetail> {

    private val tempFileProvider: TempFileProvider by inject()

    override lateinit var detailFinishedListener: DetailFinishedListener

    private val audioDetailViewModel: AudioDetailViewModel by viewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var navigation: AudioScreenNavigation? = null

    private var hasAudioRecordingPermission = false

    private val audioPlayer: AudioPlayerAdapter =
            AudioPlayerHolder().apply {
                setPlaybackInfoListener(PlaybackListener())
            }
    private val audioRecorder: AudioRecorderAdapter =
            AudioRecorderHolder(tempFileProvider.tempAudioRecordingFile).apply {
                recordingInfoListener = RecordingListener()
            }

    /**
     * true when the user is currently dragging the indicator on the seekBar
     */
    private var userIsSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioDetailViewModel.pauseSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private fun loadExistingAudioDetail() {
        val existingDetail = arguments?.getSerializable(AUDIO_DETAIL) as AudioDetail
        audioDetailViewModel.loadExistingDetail(existingDetail)
        // TODO : als encryptie geimplementeerd
        // audioPlayer.loadMedia(existingDetail.file)
    }

    private fun existingDetailGiven(): Boolean {
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

        initialiseSeekBar()

        if (existingDetailGiven()) {
            loadExistingAudioDetail()
        }

        return recordAudioBinding.root
    }

    private fun startListeners() {
        audioDetailViewModel.savedDetail.observe(this, Observer { finishedDetail ->
            if (requireActivity() is EmotionCaptureMainActivity) {
                Toast.makeText(context, R.string.save_audio_success, Toast.LENGTH_SHORT).show()
            }
            detailFinishedListener.onDetailFinished(finishedDetail)

            navigation?.backToEvent()
        })
        audioDetailViewModel.file.observe(this, Observer { file ->
            audioPlayer.loadMedia(file)
        })
        audioDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
        audioDetailViewModel.playButtonClicked.observe(this, Observer {
            audioPlayer.play()
        })
        audioDetailViewModel.pauseButtonClicked.observe(this, Observer {
            audioPlayer.pause()
        })
        audioDetailViewModel.playStopButtonClicked.observe(this, Observer {
            audioPlayer.reset()
        })
        audioDetailViewModel.recordButtonClicked.observe(this, Observer {
            audioRecorder.record()
        })
        audioDetailViewModel.recordStopButtonClicked.observe(this, Observer {
            // Initialise now so we're ready to play
            audioRecorder.stop()
            audioPlayer.loadMedia(tempFileProvider.tempAudioRecordingFile)
        })
        audioDetailViewModel.recordPauseButtonClicked.observe(this, Observer {
            audioRecorder.pause()
        })
        audioDetailViewModel.resetButtonClicked.observe(this, Observer {
            audioPlayer.reset()
            audioRecorder.reset()
        })
        audioDetailViewModel.cancelClicked.observe(this, Observer {
            audioPlayer.reset()
            audioRecorder.reset()
            activity!!.onBackPressed()
        })
    }

    private fun initialiseSeekBar() {
        recordAudioBinding.playRecording.seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            var userSelectedPosition = 0
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    userSelectedPosition = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = false
                audioPlayer.seekTo(userSelectedPosition)
            }
        })
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

    override fun onStop() {
        super.onStop()
        audioRecorder.release()
        audioPlayer.release()
    }

    interface AudioScreenNavigation {
        fun backToEvent()
    }

    inner class PlaybackListener : PlaybackInfoListener {
        override fun onDurationChanged(duration: Int) {
            recordAudioBinding.playRecording.seekBar.max = duration
            audioDetailViewModel.setRecordingFinalDuration(duration)
        }

        override fun onPositionChanged(position: Int) {
            if (!userIsSeeking) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recordAudioBinding.playRecording.seekBar.setProgress(position, true)
                } else {
                    recordAudioBinding.playRecording.seekBar.progress = position
                }
            }
        }

        override fun onPlaybackCompleted() {}
        override fun onStateChanged(state: PlaybackInfoListener.PlaybackState) {
            audioDetailViewModel.onPlayStateChanged(state)
        }

        override fun onLogUpdated(formattedMessage: String?) {
            Timber.i(formattedMessage)
        }
    }

    inner class RecordingListener : RecordingInfoListener {
        override fun onStateChanged(state: RecordingInfoListener.RecordingState) {
            audioDetailViewModel.onRecordingStateChanged(state)
        }

        override fun onRecordingDurationChanged(duration: Long) {
            audioDetailViewModel.updateRecordingTimer(duration)
        }
    }
}
