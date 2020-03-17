package be.hogent.faith.faith.backpackScreen.externalFile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentAddExternalFileBinding
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.util.TempFileProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddExternalFileFragment : Fragment(), DetailFragment<PhotoDetail> {


    private lateinit var binding: FragmentAddExternalFileBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()

    override lateinit var detailFinishedListener: DetailFinishedListener
    private var navigation: TakePhotoFragment.PhotoScreenNavigation? = null
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
        if (context is TakePhotoFragment.PhotoScreenNavigation) {
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
        externalFileViewModel.savedDetail.observe(this, Observer { newPhotoDetail ->
            Toast.makeText(context, getString(R.string.save_photo_success), Toast.LENGTH_SHORT)
                    .show()
            detailFinishedListener.onDetailFinished(newPhotoDetail)
            navigation?.backToEvent()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == RESULT_OK) {
            val fileToAdd = data!!.data

            if (fileToAdd!!.toString().contains("image")) {
                binding.selectedImage.setImageURI(fileToAdd)
                binding.videoLayout.visibility = View.GONE

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
                } catch (e: IOException) { // Catch the exception
                    e.printStackTrace()
                }

                externalFileViewModel.setCurrentFile(Uri.parse(file.absolutePath))
            } else if (fileToAdd.toString().contains("video")) {

                binding.videoView.setMediaController(binding.mediaController)
                binding.videoView.setVideoURI(fileToAdd)
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




