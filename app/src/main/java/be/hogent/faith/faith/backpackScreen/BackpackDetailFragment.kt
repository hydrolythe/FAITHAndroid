package be.hogent.faith.faith.backpackScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentContainerBaseBinding
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.video.view.ViewVideoFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ViewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.util.replaceChildFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BackpackDetailFragment : Fragment() {

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    private lateinit var saveDialog: SaveDetailDialog
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentContainerBaseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_container_base, container, false)

        backpackViewModel.showSaveDialog.observe(viewLifecycleOwner, Observer {
            if (it != null && backpackViewModel.openDetailMode.value != OpenDetailMode.EDIT)
                showSaveDialog(it)
            else
                backpackViewModel.saveCurrentDetail(userViewModel.user.value!!, it)
        })

        return binding.root
    }

    private fun showSaveDialog(detail: Detail) {
        saveDialog = SaveDetailDialog.newInstance(detail)
        saveDialog.show(requireActivity().supportFragmentManager, null)
        backpackViewModel.setCurrentFile(detail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment(backpackViewModel.currentDetail.value)
        backpackViewModel.setCurrentFile(null)
    }

    abstract fun setChildFragment(detail: Detail? = null)

    companion object {
        fun newInstance(detail: Detail): BackpackDetailFragment {
            return when (detail) {
                is TextDetail -> TextFragment.newInstance()
                is DrawingDetail -> DrawingFragment.newInstance()
                is PhotoDetail -> PhotoFragment.newInstance()
                is AudioDetail -> AudioFragment.newInstance()
                is VideoDetail -> ExternalVideoFragment.newInstance()
                is YoutubeVideoDetail -> YoutubeVideoFragment.newInstance()
                is FilmDetail -> throw UnsupportedOperationException("Film is not part of the backpack")
            }
        }
    }

    class TextFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): TextFragment {
                return TextFragment()
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

    class DrawingFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): DrawingFragment {
                return DrawingFragment()
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

    class PhotoFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): PhotoFragment {
                return PhotoFragment()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = if (detail == null) {
                TakePhotoFragment.newInstance()
            } else {
                ViewPhotoFragment.newInstance(detail as PhotoDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }

    class AudioFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): AudioFragment {
                return AudioFragment()
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

    class ExternalVideoFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): ExternalVideoFragment {
                return ExternalVideoFragment()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = ViewVideoFragment.newInstance(detail as VideoDetail)
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }

    class ExternalFileFragment : BackpackDetailFragment() {

        companion object {
            fun newInstance(): ExternalFileFragment {
                return ExternalFileFragment()
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
            } else {
                ViewYoutubeVideoFragment.newInstance(detail as YoutubeVideoDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }
}
