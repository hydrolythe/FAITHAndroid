package be.hogent.faith.faith.details.externalFile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentAddExternalFileBinding
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.details.externalFile.ExternalFileViewModel.ExternalFileType.PICTURE
import be.hogent.faith.faith.details.externalFile.ExternalFileViewModel.ExternalFileType.VIDEO
import be.hogent.faith.faith.util.TempFileProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.reflect.KClass

class AddExternalFileFragment : Fragment(), DetailFragment<Detail> {

    private lateinit var binding: FragmentAddExternalFileBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()

    override lateinit var detailFinishedListener: DetailFinishedListener
    private var navigation: ExternalFileScreenNavigation? = null
    private val tempFileProvider by inject<TempFileProvider>()
    private lateinit var previewView: View

    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_external_file, container, false)

        binding.externalFileViewModel = externalFileViewModel
        binding.lifecycleOwner = this

        selectFile()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        startListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExternalFileScreenNavigation) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        }
    }

    fun startListeners() {
        externalFileViewModel.cancelClicked.observe(this, Observer {
            navigation!!.backToEvent()
        })
        externalFileViewModel.savedDetail.observe(this, Observer { newDetail ->
            detailFinishedListener.onDetailFinished(newDetail)
            navigation?.backToEvent()
        })
        externalFileViewModel.getDetailMetaData.observe(this, Observer {
            @Suppress("UNCHECKED_CAST")
            val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity(),
                PhotoDetail::class as KClass<Detail>
            )
            if (saveDialog == null)
                externalFileViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(parentFragmentManager, null)
            }
        })
        binding.btnRemoveFile.setOnClickListener {
            removeFile()
            selectFile()
        }
    }

    private fun removeFile() {
        binding.filePreviewContainer.removeView(previewView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data!!.data != null) {
            val uriToAdd = data.data ?: return

            when (getMIMEType(uriToAdd)) {
                "image/jpeg",
                "image/bmp",
                "image/jpg",
                "image/png" -> {
                    saveAndPreviewImage(uriToAdd)
                }
                "video/mpeg",
                "video/mp4",
                "video/3gpp",
                "video/webm" -> {
                    saveAndPreviewVideo(uriToAdd)
                }
            }
        }
    }

    private fun saveAndPreviewVideo(uriToAdd: Uri) {
        Completable.fromCallable {
            val localVideoFile = writeVideoToLocalFile(uriToAdd)
            externalFileViewModel.setCurrentFile(localVideoFile, VIDEO)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingSpinner() }
            .doOnComplete {
                hideLoadingSpinner()
                previewVideo(uriToAdd)
            }.doOnError {
                hideLoadingSpinner()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_save_external_video_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .subscribe()
    }

    private fun saveAndPreviewImage(uriToAdd: Uri) {
        Completable.fromAction {
            val localImageFile = saveImage(uriToAdd)
            externalFileViewModel.setCurrentFile(localImageFile, PICTURE)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingSpinner() }
            .doOnComplete {
                hideLoadingSpinner()
                previewImage(uriToAdd)
            }.doOnError {
                hideLoadingSpinner()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_save_external_image_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .subscribe()
    }

    private fun hideLoadingSpinner() {
        binding.progress.visibility = View.GONE
        binding.btnSaveFile.visibility = View.VISIBLE
    }

    private fun showLoadingSpinner() {
        binding.progress.visibility = View.VISIBLE
        binding.btnSaveFile.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun getMIMEType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri)
    }

    private fun previewVideo(videoUri: Uri) {
        val videoView = VideoView(requireContext())
        videoView.id = View.generateViewId()
        videoView.setVideoURI(videoUri)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        binding.filePreviewContainer.addView(videoView)
        centerView(videoView)
        videoView.layoutParams.height = 0
        videoView.layoutParams.height = 0
        previewView = videoView
    }

    private fun writeVideoToLocalFile(uriToAdd: Uri): File {
        val destinationFile = tempFileProvider.tempVideoFile
        val outputStream: OutputStream = FileOutputStream(destinationFile)
        requireContext().contentResolver.openInputStream(uriToAdd)!!.use { input ->
            outputStream.use { output ->
                val buf = ByteArray(1024)
                var len: Int
                while (input.read(buf).also { len = it } > 0) {
                    output.write(buf, 0, len)
                }
            }
        }
        return destinationFile
    }

    private fun saveImage(imageURI: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageURI)
        val destinationFile = tempFileProvider.tempPhotoFile
        FileOutputStream(destinationFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return destinationFile
    }

    private fun previewImage(imageURI: Uri) {
        val imgView = ImageView(requireContext())
        imgView.id = View.generateViewId()
        imgView.setImageURI(imageURI)
        binding.filePreviewContainer.addView(imgView)
        centerView(imgView)
        previewView = imgView
    }

    private fun centerView(view: View) {
        val set = ConstraintSet()
        set.clone(binding.filePreviewContainer)
        set.connect(view.id, ConstraintSet.END, binding.btnSaveFile.id, ConstraintSet.START, 32)
        set.connect(view.id, ConstraintSet.START, binding.btnCancel.id, ConstraintSet.END, 32)
        set.connect(view.id, ConstraintSet.TOP, binding.btnCancel.id, ConstraintSet.BOTTOM, 32)
        set.connect(view.id, ConstraintSet.BOTTOM, binding.btnRemoveFile.id, ConstraintSet.TOP, 32)
        set.applyTo(binding.filePreviewContainer)
    }

    private fun selectFile() {
        val pickIntent =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(pickIntent, FILE_PICK_CODE)
    }

    companion object {
        private const val FILE_PICK_CODE = 1000
        fun newInstance(): AddExternalFileFragment {
            return AddExternalFileFragment()
        }
    }

    interface ExternalFileScreenNavigation {
        fun backToEvent()
    }

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        externalFileViewModel.setDetailsMetaData(title, dateTime)
    }
}
