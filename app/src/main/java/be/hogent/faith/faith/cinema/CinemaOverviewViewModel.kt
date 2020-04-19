package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.cinema.DeleteCinemaDetailUseCase
import be.hogent.faith.service.usecases.cinema.SaveCinemaDetailUseCase

class CinemaOverviewViewModel(
    saveBackpackDetailUseCase: SaveCinemaDetailUseCase,
    deleteBackpackDetailUseCase: DeleteCinemaDetailUseCase,
    cinema: Cinema
) : DetailsContainerViewModel<Cinema>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    cinema
) {

    private val _mijnBestandenButtonClicked = SingleLiveEvent<Unit>()
    val mijnBestandenButtonClicked: LiveData<Unit> = _mijnBestandenButtonClicked

    private val _mijnFilmpjesButtonClicked = SingleLiveEvent<Unit>()
    val mijnFilmpjesButtonClicked: LiveData<Unit> = _mijnFilmpjesButtonClicked

    private val _maakFilmButtonClicked = SingleLiveEvent<Unit>()
    val maakFilmButtonClicked: LiveData<Unit> = _maakFilmButtonClicked

    fun onMijnBestandenClicked() {
        _mijnBestandenButtonClicked.call()
    }

    fun onMijnFilmpjesClicked() {
        _mijnFilmpjesButtonClicked.call()
    }

    fun onMaakFilmClicked() {
        _maakFilmButtonClicked.call()
    }
}