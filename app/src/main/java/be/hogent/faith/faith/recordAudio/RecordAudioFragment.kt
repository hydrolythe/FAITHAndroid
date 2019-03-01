package be.hogent.faith.faith.recordAudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
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
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel.RecordingStatus.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

const val REQUESTCODE_AUDIO = 12

class RecordAudioFragment : Fragment() {

    private val recordAudioViewModel: RecordAudioViewModel by viewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var hasAudioRecordingPermission = false

    private lateinit var saveFile: File

    private lateinit var recorder: MediaRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: change with filepath from UC
        saveFile = File(context!!.filesDir, "testAudioSave")
    }

    private fun initializeRecorder() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            recorder = MediaRecorder()
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(saveFile.path)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                prepare()
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
        recordAudioViewModel.recordingStatus.observe(this, Observer { recordingStatus ->
            when (recordingStatus) {
                RECORDING -> startRecordingAudio()
                PAUSED -> recorder.stop() //TODO: find fix for lower SDK: pause is supported from 24
                STOPPED -> recorder.stop() //TODO: images dissapear when clicking (visibility conditions have to be checked)
                INITIAL -> initializeRecorder()
            }
        })
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
