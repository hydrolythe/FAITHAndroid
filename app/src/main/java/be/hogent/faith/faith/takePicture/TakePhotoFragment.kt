package be.hogent.faith.faith.takePicture

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
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentTakePhotoBinding
import be.hogent.faith.faith.util.TAG
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import org.koin.android.viewmodel.ext.android.viewModel

const val REQUESTCODE_CAMERA = 1

class TakePhotoFragment : Fragment() {
    private lateinit var navigation: TakePhotoNavigationListener
    private val takePhotoViewModel: TakePhotoViewModel by viewModel()
    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoapparat: Fotoapparat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        takePhotoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_take_photo, container, false)
        takePhotoBinding.photoViewModel = takePhotoViewModel
        takePhotoBinding.lifecycleOwner = this

        fotoapparat = Fotoapparat(
            context = this.context!!,
            view = takePhotoBinding.cameraView,
//            lensPosition = back(),
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
                        "the camera wasn't given"
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TakePhotoNavigationListener) {
            navigation = context
        }
    }

    interface TakePhotoNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
