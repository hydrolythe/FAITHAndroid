package be.hogent.faith.faith.emotionCapture.recordAudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
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
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel.RecordingStatus.PAUSED
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

const val REQUESTCODE_AUDIO = 12

class RecordAudioFragment : Fragment() {
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private val recordAudioViewModel: RecordAudioViewModel by sharedViewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var hasAudioRecordingPermission = false

    private lateinit var recorder: MediaRecorder

    private val tempFileProvider by inject<TempFileProvider>()
    /**
     * The Dialog that requests the user to enter a recordingName for the recording.
     * It is saved here so we can dismiss it once the cancel or save buttons are clicked.
     * This should normally be done in the Dialog itself but SingleLiveEvent only supports a single Listener.
     * We need one here to update the eventDetailsVM and one in the Dialog to close it.
     */
    private lateinit var saveDialog: SaveAudioRecordingDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recordAudioViewModel.pauseSupported.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private fun initializeRecorder() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            recorder = MediaRecorder().also {
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                it.setOutputFile(tempFileProvider.tempAudioRecordingFile.path)
                it.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                it.prepare()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recordAudioBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_audio, container, false)
        recordAudioBinding.apply {
            recordAudioViewModel = this@RecordAudioFragment.recordAudioViewModel
            lifecycleOwner = this@RecordAudioFragment
        }
        return recordAudioBinding.root
    }

    override fun onStart() {
        super.onStart()
        initializeRecorder()
        startListeners()
    }

    override fun onStop() {
        super.onStop()
        recorder.release()
    }

    private fun hasRecordingPermissions(): Boolean {
        hasAudioRecordingPermission =
            checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        return hasAudioRecordingPermission
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUESTCODE_AUDIO) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                hasAudioRecordingPermission = true
                initializeRecorder()
            } else {
                Toast.makeText(this.context, getString(R.string.permission_record_audio), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startListeners() {
        recordAudioViewModel.recordButtonClicked.observe(this, Observer {
            if (recordAudioViewModel.recordingStatus == PAUSED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recorder.resume()
                } else {
                    Toast.makeText(context, R.string.error_pause_not_supported, Toast.LENGTH_SHORT).show()
                }
            } else {
                startRecordingAudio()
            }
        })
        recordAudioViewModel.stopButtonClicked.observe(this, Observer {
            stopAndSaveRecording()
        })
        recordAudioViewModel.pauseButtonClicked.observe(this, Observer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.pause()
            } else {
                // TODO: pauzeerknop niet tonen bij te lage SKD?
                Toast.makeText(context, R.string.error_pause_not_supported, Toast.LENGTH_SHORT).show()
            }
        })
        recordAudioViewModel.restartButtonClicked.observe(this, Observer {
            recorder.reset()
        })
        recordAudioViewModel.recordingSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.toast_save_audio_success, Toast.LENGTH_SHORT).show()
            eventDetailsViewModel.updateEvent()
            saveDialog.dismiss()
        })
        recordAudioViewModel.recordingSaveFailed.observe(this, Observer {
            Toast.makeText(context, R.string.toast_save_audio_failed, Toast.LENGTH_SHORT).show()
            saveDialog.dismiss()
        })
    }

    private fun stopAndSaveRecording() {
        recorder.stop()
        recordAudioViewModel.tempRecordingFile = tempFileProvider.tempAudioRecordingFile
        saveDialog = SaveAudioRecordingDialogFragment.newInstance()
        saveDialog.show(fragmentManager!!, null)
    }

    private fun startRecordingAudio() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            recorder.start()
        }
    }

    companion object {
        fun newInstance(): RecordAudioFragment {
            return RecordAudioFragment()
        }
    }
}
