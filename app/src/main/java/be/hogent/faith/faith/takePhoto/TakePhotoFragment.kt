package be.hogent.faith.faith.takePhoto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import be.hogent.faith.databinding.FragmentTakePhotoBinding
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.service.usecases.TakeEventPhotoUseCase
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

/**
 * The requestcode that will be used to request photoTaker permissions
 */
const val REQUESTCODE_CAMERA = 1

/**
 * Key for this Fragments saveFile argument
 */
const val ARG_SAVEFILE: String = "SAVEFILE_LOCATION"

class TakePhotoFragment : Fragment() {
    private lateinit var navigation: TakePhotoNavigationListener

    private val takePhotoViewModel: TakePhotoViewModel by viewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoapparat: Fotoapparat

    private lateinit var saveFile: File

    private var takeEventPhotoUseCase: TakeEventPhotoUseCase = get()

    private var disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: remove once all fragments are tied together
        saveFile = File("testfoto")
        arguments?.let {
            saveFile = it.getSerializable(ARG_SAVEFILE) as File
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        takePhotoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_take_photo, container, false)
        takePhotoBinding.photoViewModel = takePhotoViewModel
        takePhotoBinding.lifecycleOwner = this

        fotoapparat = Fotoapparat(
            context = this.context!!,
            view = takePhotoBinding.cameraView,
            logger = logcat(),
            cameraErrorCallback = { Log.e(TAG, it.message) }
        )
        return takePhotoBinding.root
    }

    override fun onStart() {
        super.onStart()
        if (hasCameraPermissions()) {
            fotoapparat.start()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUESTCODE_CAMERA)
        }
        startListeners()
    }

    private fun startListeners() {
        takePhotoViewModel.emotionAvatarButtonClicked.observe(this, Observer {
            navigation.startDrawEmotionAvatarFragment()
        })
        takePhotoViewModel.takePhotoButtonClicked.observe(this, Observer {
            takeAndSavePicture()
        })
    }

    private fun takeAndSavePicture() {
        val disposable = takeEventPhotoUseCase.execute(
            TakeEventPhotoUseCase.Params(FotoApparatFacade(fotoapparat), eventDetailsViewModel.event.value!!)
        ).subscribe({
            if (this.activity != null) {
                Toast.makeText(this.activity, R.string.frag_takePhoto_saveSucces, Toast.LENGTH_SHORT).show()
                eventDetailsViewModel.updateEvent()
            }
        }, {
            if (this.activity != null) {
                Toast.makeText(this.activity, R.string.frag_takePhoto_saveFailed, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Couldn't save image: ${it.message}")
            }
        })
        disposables.add(disposable)
    }

    private fun hasCameraPermissions(): Boolean {
        return checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if requested permissions have been granted and starts the action that required the permission
     *  in the first place.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUESTCODE_CAMERA) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                fotoapparat.start()
            } else {
                Toast.makeText(this.context, R.string.permission_take_picture, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            fotoapparat.stop()
        } catch (e: IllegalStateException) {
            Log.i(
                TAG, "Stopped fotoApparat but wasn't even started. Probably because the permission to start" +
                        "the photoTaker wasn't given"
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TakePhotoNavigationListener) {
            navigation = context
        }
    }

    companion object {

        fun newInstance(): TakePhotoFragment {
            return TakePhotoFragment()
        }
    }

    interface TakePhotoNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
