package be.hogent.faith.faith.emotionCapture.takePhoto

import android.Manifest
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
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.util.TAG
import be.hogent.faith.faith.util.TempFileProvider
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * The requestcode that will be used to request photoTaker permissions
 */
const val REQUESTCODE_CAMERA = 1

class TakePhotoFragment : Fragment() {

    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private val takePhotoViewModel: TakePhotoViewModel by sharedViewModel()

    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoApparat: Fotoapparat

    private val tempFileProvider by inject<TempFileProvider>()

    /**
     * The Dialog that requests the user to enter a recordingName for the photograph.
     * It is saved here so we can dismiss it once the cancel or save buttons are clicked.
     * This should normally be done in the Dialog itself but SingleLiveEvent only supports a single Listener.
     * We need one here to update the eventDetailsVM and one in the Dialog to close it.
     */
    private lateinit var saveDialog: SavePhotoDialogFragment

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
        takePhotoViewModel.takePhotoButtonClicked.observe(this, Observer {
            takeAndSavePictureToCache()
        })
        takePhotoViewModel.recordingSaveFailed.observe(this, Observer {
            Log.e(TAG, it)
            Toast.makeText(context, getString(R.string.toast_save_photo_failed), Toast.LENGTH_SHORT).show()
            saveDialog.dismiss()
        })
        takePhotoViewModel.photoSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, getString(R.string.toast_foto_saved_success), Toast.LENGTH_SHORT).show()
            eventDetailsViewModel.updateEvent()
            saveDialog.dismiss()
        })
    }

    private fun takeAndSavePictureToCache() {
        val saveFile = tempFileProvider.tempPhotoFile
        fotoApparat.takePicture().saveToFile(saveFile).whenAvailable {
            takePhotoViewModel.tempPhotoFile = saveFile
            saveDialog = SavePhotoDialogFragment.newInstance()
            saveDialog.show(fragmentManager!!, null)
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
            Log.i(TAG, "Stopped fotoApparat but wasn't even started.")
        }
    }

    companion object {
        fun newInstance(): TakePhotoFragment {
            return TakePhotoFragment()
        }
    }
}
