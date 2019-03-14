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
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.util.TAG
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

/**
 * The requestcode that will be used to request photoTaker permissions
 */
const val REQUESTCODE_CAMERA = 1

class TakePhotoFragment : Fragment() {
    private lateinit var navigation: TakePhotoNavigationListener

    private val takePhotoViewModel: TakePhotoViewModel by viewModel {
        parametersOf(tempPhotoSaveFile, Event())
    }
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoApparat: Fotoapparat

    private lateinit var tempPhotoSaveFile: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        takePhotoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_take_photo, container, false)
        takePhotoBinding.photoViewModel = takePhotoViewModel
        takePhotoBinding.lifecycleOwner = this

        fotoApparat = Fotoapparat(
            context = this.context!!,
            view = takePhotoBinding.cameraView,
            logger = logcat(),
            cameraErrorCallback = { Log.e(TAG, it.message) }
        )
        return takePhotoBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempPhotoSaveFile = File(requireContext().cacheDir, "tempPhoto.PNG")
    }

    override fun onStart() {
        super.onStart()
        if (hasCameraPermissions()) {
            fotoApparat.start()
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
        fotoApparat.takePicture().saveToFile(tempPhotoSaveFile).whenAvailable {
            SavePhotoDialogFragment.newInstance(tempPhotoSaveFile).showNow(fragmentManager!!, null)
        }
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
                fotoApparat.start()
            } else {
                Toast.makeText(this.context, R.string.permission_take_picture, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            fotoApparat.stop()
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
