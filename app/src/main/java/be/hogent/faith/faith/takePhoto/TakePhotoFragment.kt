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
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.result.PendingResult
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.io.File

/**
 * The requestcode that will be used to request camera permissions
 */
const val REQUESTCODE_CAMERA = 1

/**
 * Key for this Fragments saveFile argument
 */
const val ARG_SAVEFILE: String = "SAVEFILE_LOCATION"

class TakePhotoFragment : Fragment() {
    private lateinit var navigation: TakePhotoNavigationListener

    private val takePhotoViewModel: TakePhotoViewModel by viewModel()
    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoapparat: Fotoapparat

    private lateinit var saveFile: File
    private var photoResult: PendingResult<BitmapPhoto>? = null

    // TODO: replace with the actual Event once all fragments are tied together
    private val event = Event(LocalDateTime.now(), "TestDescription")

    private var saveEventPhotoUseCase: SaveEventPhotoUseCase = get()

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
        fotoapparat.takePicture().toBitmap().whenAvailable { bitmapPhoto ->
            saveEventPhotoUseCase.execute(
                SaveEventPhotoUseCase.Params(
                    bitmap = bitmapPhoto!!.bitmap,
                    event = event
                )
            ).subscribe({
                Toast.makeText(this@TakePhotoFragment.context, R.string.frag_takePhoto_saveSucces, Toast.LENGTH_SHORT)
                    .show()
            }, {
                Toast.makeText(this@TakePhotoFragment.context, R.string.frag_takePhoto_saveFailed, Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "Couldn't save image: ${it.message}")
            })
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

    companion object {

        fun newInstance(): TakePhotoFragment {
            return TakePhotoFragment()
        }
    }

    interface TakePhotoNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
