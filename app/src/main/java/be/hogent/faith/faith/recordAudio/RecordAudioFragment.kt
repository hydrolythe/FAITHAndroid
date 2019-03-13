package be.hogent.faith.faith.recordAudio

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
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel.RecordingStatus.PAUSED
import be.hogent.faith.service.usecases.SaveAudioRecordingUseCase
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

const val REQUESTCODE_AUDIO = 12

class RecordAudioFragment : Fragment() {

    private val recordAudioViewModel: RecordAudioViewModel by viewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var recordAudioBinding: FragmentRecordAudioBinding

    private var hasAudioRecordingPermission = false

    private lateinit var tempRecordingFile: File

    private lateinit var recorder: MediaRecorder

    private var saveAudioUseCase: SaveAudioRecordingUseCase = get()

    private var disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The recording is temporarily saved in the cache directory, and then moved to
        // permanent storage. More info in the [SaveAudioRecordingUseCase].
        tempRecordingFile = File(context!!.cacheDir, "TempAudioRecording.3gpp")

        recordAudioViewModel.pauseSupported.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private fun initializeRecorder() {
        if (!hasRecordingPermissions()) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUESTCODE_AUDIO)
        } else {
            recorder = MediaRecorder().also {
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                it.setOutputFile(tempRecordingFile.path)
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
        disposables.clear()
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
                    Toast.makeText(context, "Pauzeren wordt door je apparaat niet ondersteund", Toast.LENGTH_SHORT)
                        .show()
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
                //TODO: pauzeerknop niet tonen bij te lage SKD?
                Toast.makeText(context, "Pauzeren wordt door je apparaat niet ondersteund", Toast.LENGTH_SHORT).show()
            }
        })
        recordAudioViewModel.restartButtonClicked.observe(this, Observer {
            recorder.reset()
        })
    }

    private fun stopAndSaveRecording() {
        recorder.stop()
        val disposable = saveAudioUseCase.execute(
            SaveAudioRecordingUseCase.SaveAudioRecordingParams(
                tempRecordingFile,
                eventDetailsViewModel.event.value!!
            )
        ).subscribe({
            Toast.makeText(context, "Opgeslagen!", Toast.LENGTH_SHORT).show()

        }, {
            Toast.makeText(context, "Fout bij opslaan! ${it.message}", Toast.LENGTH_SHORT).show()
        })
        disposables.add(disposable)
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
