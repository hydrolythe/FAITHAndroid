package be.hogent.faith.faith.cinema

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.models.Cinema
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.detail.FilmDetail
import be.hogent.faith.faith.util.SingleLiveEvent

class CinemaOverviewViewModel(
    cinema: Cinema,
    cinemaRepository: CinemaRepository,
    context:Context
) : DetailsContainerViewModel<Cinema>(
    cinema,
    cinemaRepository,
    context
) {

    override val details: List<Detail>
        get() = if (_filmsVisible.value!!) detailsContainer.films else detailsContainer.getFiles()

    private val _filmsVisible = MutableLiveData<Boolean>().apply {
        this.value = true
    }
    val filmsVisible: LiveData<Boolean> = _filmsVisible

    private val _makeFilmButtonClicked = SingleLiveEvent<Unit>()
    val makeFilmButtonClicked: LiveData<Unit> = _makeFilmButtonClicked

    private val _filmSavedSuccessFully = SingleLiveEvent<Unit>()
    val filmSavedSuccessFully: LiveData<Unit> = _filmSavedSuccessFully

    fun onFilesButtonClicked() {
        _filmsVisible.value = false
        initSearch()
    }

    fun onFilmsButtonClicked() {
        _filmsVisible.value = true
        initSearch()
    }

    fun onMakeFilmClicked() {
        if (_filmsVisible.value!!) {
            _filmsVisible.value = false
            initSearch()
        }
        _makeFilmButtonClicked.call()
    }

    private fun initSearch() {
        setSearchStringText("")
        resetDateRange()
    }

    fun saveFilm(filmDetail: FilmDetail, user: User) {
        startLoading()
    }
}