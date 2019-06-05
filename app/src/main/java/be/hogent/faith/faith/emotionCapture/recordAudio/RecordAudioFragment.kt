package be.hogent.faith.faith.emotionCapture.recordAudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordStateStopped
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.IOException

const val REQUESTCODE_AUDIO = 12

class RecordAudioFragment : Fragment() {
    private val eventViewModel: EventViewModel by sharedViewModel()

    // TODO: check if this still needs to be a shared VM
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recordAudioViewModel.pauseSupported.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
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
        checkAudioRecordingPermission()
        //TODO: find a better location possibly?
        // Strange to pass recordAudioVM to itself, but it can't create Android objects
        recordAudioViewModel.setState(
            RecordStateStopped(
                recordAudioViewModel,
                MediaRecorder(),
                MediaPlayer(),
                tempFileProvider
            )
        )
        startListeners()
    }

    override fun onStop() {
        super.onStop()
        recorder.release()
    }

    private fun hasRecordingPermissions(): Boolean {
        return checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAudioRecordingPermission() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUESTCODE_AUDIO) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                hasAudioRecordingPermission = true
                checkAudioRecordingPermission()
            } else {
                Toast.makeText(this.context, getString(R.string.permission_record_audio), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startListeners() {
        //TODO: convert to also use a state for play/pause/restart
        recordAudioBinding.btnRecordAudioPlay.setOnClickListener {
            MediaPlayer().apply {
                try {
                    setDataSource(tempFileProvider.tempAudioRecordingFile.path)
                    prepare()
                    start()
                    Log.d(TAG, "Started playing audio from ${tempFileProvider.tempAudioRecordingFile.path}")
                } catch (e: IOException) {
                    Log.e(TAG, "Preparing audio playback failed")
                }
            }
        }
        recordAudioViewModel.saveButtonClicked.observe(this, Observer {
            eventViewModel.saveAudio(tempFileProvider.tempAudioRecordingFile)
        })
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
