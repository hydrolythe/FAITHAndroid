package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.cinema.GetCinemaDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableObserver

class CinemaOverviewViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Cinema>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Cinema>,
    loadDetailFileUseCase: LoadDetailFileUseCase<Cinema>,
    cinema: Cinema,
    private val getCinemaDataUseCase: GetCinemaDataUseCase
) : DetailsContainerViewModel<Cinema>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    loadDetailFileUseCase,
    cinema
) {

    private val _filmsVisible = MutableLiveData<Boolean>().apply {
        this.value = true
    }
    val filmsVisible: LiveData<Boolean> = _filmsVisible

    private val _makeFilmButtonClicked = SingleLiveEvent<Unit>()
    val makeFilmButtonClicked: LiveData<Unit> = _makeFilmButtonClicked

    private val _addButtonClicked = SingleLiveEvent<Unit>()
    val addButtonClicked: LiveData<Unit> = _addButtonClicked

    fun onFilesButtonClicked() {
        getCinemaDataUseCase.execute(
            GetCinemaDataUseCase.Params(),
            object : DisposableObserver<List<Detail>>() {
                override fun onComplete() {}

                override fun onNext(t: List<Detail>) {
                    details = t
                }

                override fun onError(e: Throwable) {
                    _errorMessage.postValue(R.string.error_load_cinema)
                }
            })
        _filmsVisible.value = false
    }

    fun onFilmsButtonClicked() {
        details = detailsContainer.films
        _filmsVisible.value = true
    }

    fun onMakeFilmClicked() {
        _makeFilmButtonClicked.call()
    }

    fun onAddButtonClicked() {
        _addButtonClicked.call()
    }

    init {
        searchString.value = ""
    }
}