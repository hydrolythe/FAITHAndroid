package be.hogent.faith.faith.details.photo.create

import android.Manifest
import android.content.Context
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
import be.hogent.faith.databinding.FragmentTakePhotoBinding
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.util.TempFileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import kotlinx.android.synthetic.main.fragment_take_photo.img_takePhoto_Photo
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

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

        takePhotoViewModel.photoSaveFile.observe(this, Observer { photo ->
            if (photo != null) {
                Glide.with(context!!).load(photo).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img_takePhoto_Photo)
                Timber.d("photoSaveFile saved ${photo.name} ${photo.path}")
            }
        })

        takePhotoViewModel.savedDetail.observe(this, Observer { newPhotoDetail ->
            Toast.makeText(context, getString(R.string.save_photo_success), Toast.LENGTH_SHORT)
                .show()
            detailFinishedListener.onDetailFinished(newPhotoDetail)
            navigation?.backToEvent()
        })
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
        return checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PERMISSION_GRANTED
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