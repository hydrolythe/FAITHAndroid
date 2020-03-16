package be.hogent.faith.faith.backpackScreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentAddExternalFileBinding



class AddExternalFileFragment : Fragment(){


    private lateinit var binding: FragmentAddExternalFileBinding
    private var fileToAdd: Uri? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_add_external_file, container, false)

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (fileToAdd == null) {
            val pickIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            startActivityForResult(pickIntent, FILE_PICK_CODE)

        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            fileToAdd = data!!.data
            if (fileToAdd.toString().contains("image")) {
                binding.imageView3.setImageURI(fileToAdd)
                binding.videoLayout.visibility = View.GONE
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


}


