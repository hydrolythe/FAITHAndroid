package be.hogent.faith.faith.details.photo.create

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentTakePhotoBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.backpackScreen.BackpackScreenActivity
import be.hogent.faith.faith.cinema.CinemaActivity
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.util.TempFileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import kotlinx.android.synthetic.main.fragment_take_photo.img_takePhoto_theTakenPhoto
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The requestcode that will be used to request photoTaker permissions
 */
const val REQUESTCODE_CAMERA = 1

class TakePhotoFragment : Fragment(), DetailFragment<PhotoDetail> {

    override lateinit var detailFinishedListener: DetailFinishedListener

    private val takePhotoViewModel: TakePhotoViewModel by viewModel()

    private lateinit var takePhotoBinding: FragmentTakePhotoBinding

    private lateinit var fotoApparat: Fotoapparat

    private var navigation: PhotoScreenNavigation? = null

    private val tempFileProvider by inject<TempFileProvider>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        takePhotoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_take_photo, container, false)
        takePhotoBinding.photoViewModel = takePhotoViewModel
        takePhotoBinding.lifecycleOwner = this

        fotoApparat = Fotoapparat(
            context = requireContext(),
            view = takePhotoBinding.cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = logcat()
        )
        return takePhotoBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PhotoScreenNavigation) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        }
    }

    override fun onStart() {
        super.onStart()
        if (hasCameraPermissions()) {
            fotoApparat.start()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUESTCODE_CAMERA
            )
        }
        startListeners()
    }

    private fun startListeners() {
        takePhotoViewModel.takePhotoButtonClicked.observe(this, Observer {
            takeAndSavePictureToCache()
        })

        takePhotoViewModel.frontSelected.observe(this, Observer {
            flipCamera()
        })

        takePhotoViewModel.photoSaveFile.observe(this, Observer { photo ->
            if (photo != null) {
                Glide.with(requireContext()).load(photo).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img_takePhoto_theTakenPhoto)
                Timber.d("photoSaveFile saved ${photo.name} ${photo.path}")
            }
        })

        takePhotoViewModel.getDetailMetaData.observe(this, Observer {
            @Suppress("UNCHECKED_CAST") val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity(),
                PhotoDetail::class as KClass<Detail>
            )
            if (saveDialog == null)
                takePhotoViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(parentFragmentManager, null)
            }
        })

        takePhotoViewModel.savedDetail.observe(this, Observer { newPhotoDetail ->
            if (requireActivity() is EmotionCaptureMainActivity) {
                Toast.makeText(context, getString(R.string.save_photo_success), Toast.LENGTH_SHORT)
                    .show()
            }
            detailFinishedListener.onDetailFinished(newPhotoDetail)
            navigation?.backToEvent()
        })

        takePhotoViewModel.cancelClicked.observe(this, Observer {
            showExitAlert()
        })
    }

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        takePhotoViewModel.setDetailsMetaData(title, dateTime)
    }

    /**
     * to take a frontSelected
     */
    private fun flipCamera() {
        fotoApparat.switchTo(
            if (takePhotoViewModel.frontSelected.value != null && takePhotoViewModel.frontSelected.value == true) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )
    }

    private fun takeAndSavePictureToCache() {
        val saveFile = tempFileProvider.tempPhotoFile
        /**
         * Saving the photoSaveFile is triggered here instead of from the VM because then it would need to hold
         * the tempPhotoFile.
         */
        fotoApparat.takePicture().saveToFile(saveFile).whenAvailable {
            takePhotoViewModel.photoTaken(saveFile)
        }
    }

    private fun hasCameraPermissions(): Boolean {
        return checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PERMISSION_GRANTED
    }

    private fun showExitAlert() {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this.requireContext()).apply {
                when (requireActivity()) {
                    is BackpackScreenActivity -> setTitle(R.string.dialog_to_the_backpack)
                    is CinemaActivity -> setTitle(R.string.dialog_to_the_cinema_title)
                    else -> setTitle(R.string.dialog_to_the_event_title)
                }
                setMessage(R.string.dialog_takePhoto_cancel_message)
                setPositiveButton(R.string.ok) { _, _ ->
                    navigation!!.backToEvent()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    /**
     * Checks if requested permissions have been granted and starts the action that required the permission
     *  in the first place.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUESTCODE_CAMERA) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                fotoApparat.start()
            } else {
                Toast.makeText(this.context, R.string.permission_take_picture, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            fotoApparat.stop()
        } catch (e: IllegalStateException) {
            Timber.i("Stopped fotoApparat but wasn't even started.")
        }
    }

    companion object {
        fun newInstance(): TakePhotoFragment {
            return TakePhotoFragment()
        }
    }

    interface PhotoScreenNavigation {
        fun backToEvent()
    }
}
