package be.hogent.faith.faith.cinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.*
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpackScreen.DeleteDetailDialog
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.video.view.ViewVideoFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CinemaActivity : AppCompatActivity(), CinemaStartScreenFragment.CinemaNavigationListener,
    DetailViewHolder.ExistingDetailNavigationListener,
    DetailFinishedListener,
    DeleteDetailDialog.DeleteDetailDialogListener,
    TakePhotoFragment.PhotoScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
    AddExternalFileFragment.ExternalFileScreenNavigation,
    CinemaCreateVideoFragment.CinemaCreateVideoFragmentNavigationListener {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val cinemaOverviewViewModel: CinemaOverviewViewModel by viewModel {
        parametersOf(
            userViewModel.user.value!!.cinema
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema)

        if (savedInstanceState == null) {
            val fragment = CinemaStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.cinema_fragment_container, fragment)
                .commit()
        }

        cinemaOverviewViewModel.goToCityScreen.observe(this, Observer {
            closeCinema()
        })

        cinemaOverviewViewModel.goToDetail.observe(this, Observer {
            replaceFragment(
                CinemaDetailFragment.newInstance(it),
                R.id.cinema_fragment_container
            )
        })
    }

    override fun closeCinema() {
        closeCinemaSpecificScopes()
        finish()
    }

    private fun closeCinemaSpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(
            CinemaDetailFragment.PhotoFragment.newInstance(),
            R.id.cinema_fragment_container
        )
        cinemaOverviewViewModel.setOpenDetailType(OpenDetailMode.NEW)
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(
            CinemaDetailFragment.DrawingFragment.newInstance(),
            R.id.cinema_fragment_container
        )
        cinemaOverviewViewModel.setOpenDetailType(OpenDetailMode.NEW)
    }

    override fun startExternalFileDetailFragment() {
        replaceFragment(
            CinemaDetailFragment.ExternalFileFragment.newInstance(),
            R.id.cinema_fragment_container
        )
        cinemaOverviewViewModel.setOpenDetailType(OpenDetailMode.NEW)
    }

    override fun startCreateFilmFragment() {
        replaceFragment(CinemaCreateVideoFragment.newInstance(), R.id.cinema_fragment_container)
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is VideoDetail -> save(detail)
        }
        // cinemaOverviewViewModel.viewButtons(true)
    }

    fun save(detail: Detail) {
        cinemaOverviewViewModel.showSaveDialog(detail)
    }

    override fun startViewVideoFragment(detail: FilmDetail) {
        replaceFragment(ViewVideoFragment.newInstance(detail), R.id.cinema_fragment_container)
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

    override fun openDetailScreenFor(detail: Detail) {
        cinemaOverviewViewModel.setOpenDetailType(OpenDetailMode.EDIT)
        cinemaOverviewViewModel.setCurrentFileAndLoadCorrespondingFile(detail)
        replaceFragment(CinemaDetailFragment.newInstance(detail), R.id.cinema_fragment_container)
        // cinemaOverviewViewModel.viewButtons(false)
    }

    override fun deleteDetail(detail: Detail) {
        val dialog = DeleteDetailDialog.newInstance(detail)
        dialog.show(supportFragmentManager, "DeleteDetailDialog")
    }

    override fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail) {
        cinemaOverviewViewModel.deleteDetail(detail)
    }

    override fun onDetailCancelClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun backToEvent() {
        supportFragmentManager.popBackStack()
        cinemaOverviewViewModel.onFilesButtonClicked()
    }
}
