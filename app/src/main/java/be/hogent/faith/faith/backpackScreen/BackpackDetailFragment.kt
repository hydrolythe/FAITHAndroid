package be.hogent.faith.faith.backpackScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.backpackScreen.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.externalVideo.view.ViewExternalVideoFragment
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.backpackScreen.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.backpackScreen.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.util.replaceChildFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BackpackDetailFragment : Fragment() {

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    private lateinit var editDetailBinding: be.hogent.faith.databinding.FragmentEditFileBinding
    private lateinit var saveDialog: SaveDetailDialog
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_file, container, false)
        editDetailBinding.lifecycleOwner = this

        backpackViewModel.showSaveDialog.observe(this, Observer {
            if (it != null && backpackViewModel.openDetailType.value != OpenDetailType.EDIT)
                showSaveDialog(it)
            else
                backpackViewModel.saveCurrentDetail(userViewModel.user.value!!, it)
        })

        return editDetailBinding.root
    }

    private fun showSaveDialog(detail: Detail) {
        saveDialog = SaveDetailDialog.newInstance(detail)
        saveDialog.show(fragmentManager!!, null)
        backpackViewModel.setCurrentFile(detail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment(backpackViewModel.currentFile.value)
        backpackViewModel.setCurrentFile(null)
    }

    abstract fun setChildFragment(detail: Detail? = null)

    companion object {
        fun newInstance(detail: Detail): BackpackDetailFragment {
            return when (detail) {
                is TextDetail -> TextFragmentNoEmotionAvatar.newInstance()
                is DrawingDetail -> DrawingFragmentNoEmotionAvatar.newInstance()
                is PhotoDetail -> PhotoFragmentNoEmotionAvatar.newInstance()
                is AudioDetail -> AudioFragmentNoEmotionAvatar.newInstance()
                is ExternalVideoDetail -> ExternalVideoFragmentNoEmotionAvatar.newInstance()
                is YoutubeVideoDetail -> YoutubeVideoFragment.newInstance()
            }
        }
    }

    class TextFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): TextFragmentNoEmotionAvatar {
                return TextFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
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

    class ExternalVideoFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): ExternalVideoFragmentNoEmotionAvatar {
                return ExternalVideoFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = ViewExternalVideoFragment.newInstance(detail as ExternalVideoDetail)
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
    class ExternalFileFragmentNoEmotionAvatar : BackpackDetailFragment() {

        companion object {
            fun newInstance(): ExternalFileFragmentNoEmotionAvatar {
                return ExternalFileFragmentNoEmotionAvatar()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = AddExternalFileFragment.newInstance()
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
    class YoutubeVideoFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): YoutubeVideoFragment {
                return YoutubeVideoFragment()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = if (detail == null) {
                YoutubeVideoDetailFragment.newInstance()
            }
            else{
                ViewYoutubeVideoFragment.newInstance(detail as YoutubeVideoDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
}
