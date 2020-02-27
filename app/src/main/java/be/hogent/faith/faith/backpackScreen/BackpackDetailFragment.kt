package be.hogent.faith.faith.backpackScreen


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.util.replaceChildFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel


abstract class BackpackDetailFragment : Fragment() {

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    private lateinit var editDetailBinding: be.hogent.faith.databinding.FragmentEditFileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_file, container, false)
        editDetailBinding.lifecycleOwner = this

        return editDetailBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment(backpackViewModel.currentFile.value)
        backpackViewModel.setCurrentFile(null)
    }

    abstract fun setChildFragment(detail: Detail? = null)

    companion object {
        private const val DETAIL = "The detail to show"
        fun newInstance(detail: Detail): BackpackDetailFragment {
            return when (detail) {
                is TextDetail -> TextFragmentNoEmotionAvatar.newInstance(detail)
                is DrawingDetail -> DrawingFragmentNoEmotionAvatar.newInstance(detail)
                is PhotoDetail -> PhotoFragmentNoEmotionAvatar.newInstance(detail)
                is AudioDetail -> AudioFragmentNoEmotionAvatar.newInstance(detail)
                else -> TextFragmentNoEmotionAvatar.newInstance(
                    detail as TextDetail
                )
            }

        }
    }


    class TextFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): TextFragmentNoEmotionAvatar {
                return TextFragmentNoEmotionAvatar()
            }

            fun newInstance(textDetail: TextDetail): TextFragmentNoEmotionAvatar {
                return TextFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
          //  val detail = arguments?.getSerializable(DETAIL) as TextDetail?
            val childFragment = if (detail == null) {
                TextDetailFragment.newInstance()
            } else {
                TextDetailFragment.newInstance(detail as TextDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }

    class DrawingFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): DrawingFragmentNoEmotionAvatar {
                return DrawingFragmentNoEmotionAvatar()
            }

            fun newInstance(drawingDetail: DrawingDetail): DrawingFragmentNoEmotionAvatar {
                return DrawingFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = if (detail == null) {
                DrawingDetailFragment.newInstance()
            } else {
                DrawingDetailFragment.newInstance(detail as DrawingDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
    class PhotoFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): PhotoFragmentNoEmotionAvatar {
                return PhotoFragmentNoEmotionAvatar()
            }

            fun newInstance(photoDetail: PhotoDetail): PhotoFragmentNoEmotionAvatar {
                return PhotoFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = if (detail == null) {
                TakePhotoFragment.newInstance()
            } else {
                ReviewPhotoFragment.newInstance(detail as PhotoDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
    class AudioFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): AudioFragmentNoEmotionAvatar {
                return AudioFragmentNoEmotionAvatar()
            }

            fun newInstance(audioDetail: AudioDetail): AudioFragmentNoEmotionAvatar {
                return AudioFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = if (detail == null) {
                RecordAudioFragment.newInstance()
            } else {
                RecordAudioFragment.newInstance(detail as AudioDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
}

