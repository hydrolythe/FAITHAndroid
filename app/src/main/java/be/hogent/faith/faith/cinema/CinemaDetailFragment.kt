package be.hogent.faith.faith.cinema

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEditFileBinding
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpackScreen.SaveDetailDialog
import be.hogent.faith.faith.backpackScreen.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.externalVideo.view.ViewExternalVideoFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.util.replaceChildFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class CinemaDetailFragment : Fragment() {

    private val cinemaViewModel: CinemaOverviewViewModel by sharedViewModel()
    private lateinit var editDetailBinding: FragmentEditFileBinding
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

        cinemaViewModel.showSaveDialog.observe(this, Observer {
            if (it != null && cinemaViewModel.openDetailMode.value != OpenDetailMode.EDIT)
                showSaveDialog(it)
            else
                cinemaViewModel.saveCurrentDetail(userViewModel.user.value!!, it)
        })

        return editDetailBinding.root
    }

    private fun showSaveDialog(detail: Detail) {

        saveDialog = SaveDetailDialog.newInstance(detail)
        saveDialog.show(fragmentManager!!, null)
        cinemaViewModel.setCurrentFile(detail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment(cinemaViewModel.currentFile.value)
        cinemaViewModel.setCurrentFile(null)
    }

    abstract fun setChildFragment(detail: Detail? = null)

    companion object {
        fun newInstance(detail: Detail): CinemaDetailFragment {
            return when (detail) {
                is TextDetail -> throw UnsupportedOperationException("Film is not part of the backpack")
                is DrawingDetail -> DrawingFragment.newInstance()
                is PhotoDetail -> PhotoFragment.newInstance()
                is AudioDetail -> throw UnsupportedOperationException("Film is not part of the backpack")
                is ExternalVideoDetail -> ExternalVideoFragment.newInstance()
                is YoutubeVideoDetail -> throw UnsupportedOperationException("Film is not part of the backpack")
                is FilmDetail -> throw UnsupportedOperationException("Film is not part of the backpack")
            }
        }
    }

    class DrawingFragment : CinemaDetailFragment() {

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

    class PhotoFragment : CinemaDetailFragment() {

        companion object {
            fun newInstance(): PhotoFragment {
                return PhotoFragment()
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

    class ExternalVideoFragment : CinemaDetailFragment() {

        companion object {
            fun newInstance(): ExternalVideoFragment {
                return ExternalVideoFragment()
            }
        }

        override fun setChildFragment(detail: Detail?) {
            val childFragment = ViewExternalVideoFragment.newInstance(detail as ExternalVideoDetail)
            replaceChildFragment(childFragment, R.id.fragment_container_editFile)
        }
    }

    class ExternalFileFragment : CinemaDetailFragment() {

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
}
