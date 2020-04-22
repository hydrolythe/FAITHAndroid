package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase

class CinemaOverviewViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Cinema>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Cinema>,
    cinema: Cinema
) : DetailsContainerViewModel<Cinema>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    cinema
) {

    private val _filesButtonClicked = SingleLiveEvent<Unit>()
    val filesButtonClicked: LiveData<Unit> = _filesButtonClicked

    private val _filmsButtonClicked = SingleLiveEvent<Unit>()
    val filmsButtonClicked: LiveData<Unit> = _filmsButtonClicked

    private val _makeFilmButtonClicked = SingleLiveEvent<Unit>()
    val maakFilmButtonClicked: LiveData<Unit> = _makeFilmButtonClicked

    private val _addButtonClicked = SingleLiveEvent<Unit>()
    val addButtonClicked: LiveData<Unit> = _addButtonClicked

    fun onFilesButtonClicked() {
            details = detailsContainer.getFiles()
            _filesButtonClicked.call()
    }

    fun onFilmsButtonClicked() {
        details = detailsContainer.films
        _filmsButtonClicked.call()
    }

    fun onMakeFilmClicked() {
        _makeFilmButtonClicked.call()
    }

    fun onAddButtonClicked() {
        _addButtonClicked.call()
    }

    init {
        searchString.value = "test"
    }
}