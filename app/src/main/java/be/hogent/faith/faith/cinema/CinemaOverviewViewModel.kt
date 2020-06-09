package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.cinema.AddFilmToCinemaUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import org.threeten.bp.LocalDate
import timber.log.Timber

class CinemaOverviewViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Cinema>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Cinema>,
    loadDetailFileUseCase: LoadDetailFileUseCase<Cinema>,
    cinema: Cinema,
    getCinemaDataUseCase: GetDetailsContainerDataUseCase<Cinema>,
    private val addFilmToCinemaUseCase: AddFilmToCinemaUseCase
) : DetailsContainerViewModel<Cinema>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    loadDetailFileUseCase,
    getCinemaDataUseCase,
    cinema
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

    fun resetDateRange() {
        _startDate.value = LocalDate.MIN.plusDays(1)
        _endDate.value = LocalDate.now()
    }

    fun saveFilm(filmDetail: FilmDetail, user: User) {
        val params = AddFilmToCinemaUseCase.Params(filmDetail, detailsContainer, user)
        addFilmToCinemaUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _filmSavedSuccessFully.call()
                forceDetailsUpdate()
            }

            override fun onError(e: Throwable) {
                Timber.e(e)
                e.printStackTrace()
                _errorMessage.postValue(R.string.save_film_error)
            }
        })
    }
}