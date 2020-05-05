package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import org.threeten.bp.LocalDate

class CinemaOverviewViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Cinema>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Cinema>,
    loadDetailFileUseCase: LoadDetailFileUseCase<Cinema>,
    cinema: Cinema,
    getCinemaDataUseCase: GetDetailsContainerDataUseCase<Cinema>
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

    private val _addButtonClicked = SingleLiveEvent<Unit>()
    val addButtonClicked: LiveData<Unit> = _addButtonClicked

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

    fun onAddButtonClicked() {
        _addButtonClicked.call()
    }

    private fun initSearch() {
        setSearchStringText("")
        resetDateRange()
    }

    fun resetDateRange() {
        _startDate.value = LocalDate.MIN.plusDays(1)
        _endDate.value = LocalDate.now()
    }
}