package be.hogent.faith.faith.backpackScreen.externalFile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentAddExternalFileBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class AddExternalFileFragment : Fragment(), DetailFragment<Detail> {

    private lateinit var binding: FragmentAddExternalFileBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()

    override lateinit var detailFinishedListener: DetailFinishedListener
    private var navigation: ExternalFileScreenNavigation? = null
    private val tempFileProvider by inject<TempFileProvider>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_add_external_file, container, false)

        binding.externalFileViewModel = externalFileViewModel
        binding.lifecycleOwner = this

        val pickIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(pickIntent, FILE_PICK_CODE)
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
            if (newDetail is PhotoDetail) {
                Toast.makeText(context, getString(R.string.save_photo_success), Toast.LENGTH_SHORT)
                        .show()
            } else {
                Toast.makeText(context, getString(R.string.save_video_success), Toast.LENGTH_SHORT)
                        .show()
            }

            detailFinishedListener.onDetailFinished(newDetail)
            navigation?.backToEvent()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            val uriToAdd = data!!.data

            if (uriToAdd!!.toString().contains("image")) {
                binding.selectedImage.setImageURI(uriToAdd)
                binding.videoView.visibility = View.GONE

                val drawable = binding.selectedImage.drawable
                // Get the bitmap from drawable object
                val bitmap = (drawable as BitmapDrawable).bitmap

                // Create a file to save the image
                val file = tempFileProvider.tempPhotoFile
                try {
                    // Get the file output stream
                    val stream: OutputStream = FileOutputStream(file)
                    // Compress bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    // Flush the stream
                    stream.flush()
                    // Close stream
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                externalFileViewModel.setCurrentFile(file)
            } else if (uriToAdd.toString().contains("video")) {
                val file = tempFileProvider.tempExternalVideoFile
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uriToAdd)
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(4096)
                var len: Int
                while (inputStream!!.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream.close()
                val mediaController = MediaController(requireContext())
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoURI(uriToAdd)

                externalFileViewModel.setCurrentFile(file)
            }
        }
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
