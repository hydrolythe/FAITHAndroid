package be.hogent.faith.faith.details.externalFile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.util.TempFileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

class AddExternalFileFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentAddExternalFileBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()

    private lateinit var detailFinishedListener: DetailFinishedListener
    private var navigation: ExternalFileScreenNavigation? = null
    private val tempFileProvider by inject<TempFileProvider>()
    private lateinit var previewView: View
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mJob = Job()
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
            @Suppress("UNCHECKED_CAST") val saveDialog = DetailsFactory.createMetaDataDialog(requireActivity(), PhotoDetail::class as KClass<Detail>)
            if (saveDialog == null)
                externalFileViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22) // in case of fragment to activity communication we do not need this line. But must write this i case of fragment to fragment communication
                saveDialog.show(getParentFragmentManager(), null)
            } })
        binding.btnRemoveFile.setOnClickListener {
            removeFile()
            selectFile()
        }
    }

    private fun removeFile() {
        binding.filePreviewContainer.removeView(previewView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val uriToAdd = data!!.data

            if (uriToAdd!!.toString().contains("image")) {
                val localFile = saveAndPreviewImage(uriToAdd)
                externalFileViewModel.setCurrentFile(localFile)
            } else if (uriToAdd.toString().contains("video")) {
                try {
                    launch {
                        // Zolang het niet omgezet werd naar een file zal een progressbard verschijnen i.p.v de save button
                        binding.progress.visibility = View.VISIBLE
                        binding.btnSaveFile.visibility = View.GONE
                        val localVideoFile = writeVideoToLocalFile(uriToAdd)
                        binding.progress.visibility = View.GONE
                        binding.btnSaveFile.visibility = View.VISIBLE
                        setUpVideoPreview(uriToAdd)
                        externalFileViewModel.setCurrentFile(localVideoFile)
                    }
                } catch (e: IOException) {
                    Timber.e(e)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_save_external_video_failed),
                        Toast.LENGTH_SHORT
                    ).show()

                    navigation?.backToEvent()
                }
            }
        } else {
            navigation!!.backToEvent()
        }
    }

    private fun setUpVideoPreview(uriToAdd: Uri?) {
        val videoView = VideoView(requireContext())
        videoView.id = View.generateViewId()
        videoView.setVideoURI(uriToAdd)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        binding.filePreviewContainer.addView(videoView)
        centerView(videoView)
        videoView.layoutParams.height = 480
        videoView.layoutParams.height = 640
        previewView = videoView
    }

    private suspend fun writeVideoToLocalFile(uriToAdd: Uri): File {
        val job = async(Dispatchers.Default) {
            val inputStream: InputStream? =
                requireContext().contentResolver.openInputStream(uriToAdd)
            val outputStream: OutputStream = FileOutputStream(tempFileProvider.tempVideoFile)
            val buf = ByteArray(4096)
            var len: Int

            while (inputStream!!.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        }
        job.await()
        return tempFileProvider.tempVideoFile
    }

    private fun saveAndPreviewImage(uriToAdd: Uri?): File {
        // Load URi into an imageview
        val imgView = ImageView(requireContext())
        imgView.id = View.generateViewId()
        imgView.setImageURI(uriToAdd)
        binding.filePreviewContainer.addView(imgView)
        centerView(imgView)
        previewView = imgView

        // Use the imageview as source for a drawable we can write to a file
        val drawable = imgView.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        val localImageFile = tempFileProvider.tempPhotoFile
        try {
            val stream: OutputStream = FileOutputStream(localImageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.error_save_photo_failed), Toast.LENGTH_SHORT)
                .show()
        }
        return localImageFile
    }

    private fun centerView(view: View) {
        val set = ConstraintSet()
        set.clone(binding.filePreviewContainer)
        set.connect(view.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
        set.connect(view.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
        set.connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        set.connect(view.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
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
}
