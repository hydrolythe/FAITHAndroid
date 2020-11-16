package be.hogent.faith.faith.cinema

import android.os.Bundle
import be.hogent.faith.R
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.faith.detailscontainer.DetailsContainerActivity
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CinemaActivity : DetailsContainerActivity<Cinema>(),
    CinemaStartScreenFragment.CinemaNavigationListener,
    CinemaCreateVideoFragment.CinemaCreateVideoFragmentNavigationListener {

    override val activityResourceId: Int = R.layout.activity_cinema
    override val fragmentContainerResourceId: Int = R.id.cinema_fragment_container
    override val detailsContainerViewModel: DetailsContainerViewModel<Cinema>
        get() = cinemaOverviewViewModel

    private lateinit var cinemaOverviewViewModel: CinemaOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cinemaOverviewViewModel = getViewModel { parametersOf(userViewModel.user.value!!.cinema) }
    }

    override fun createNewFragmentInstance() = CinemaStartScreenFragment.newInstance()

    override fun startCreateFilmFragment() {
        replaceFragment(CinemaCreateVideoFragment.newInstance(), fragmentContainerResourceId)
    }

    override fun onDetailFinished(detail: Detail) {
        saveToTimeLine(detail)
    }

    private fun saveToTimeLine(detail: Detail) {
        cinemaOverviewViewModel.saveCurrentDetail(userViewModel.user.value!!, detail)
    }

    override fun onFilmFinished(detail: FilmDetail) {
        cinemaOverviewViewModel.saveFilm(detail, userViewModel.user.value!!)
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

    override fun openDetailScreenFor(detail: Detail) {
        detailsContainerViewModel.setOpenDetailType(OpenDetailMode.VIEW)
        detailsContainerViewModel.setCurrentFileAndLoadCorrespondingFile(detail)
    }
}
