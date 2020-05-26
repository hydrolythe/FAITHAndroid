package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status.Completed
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status.InProgress
import be.hogent.faith.service.usecases.cinema.VideoEncoder
import io.reactivex.observers.DisposableObserver

class CinemaCreateVideoViewModel(
    private val createCinemaVideoUseCase: CreateCinemaVideoUseCase
) : ViewModel() {

    private val _selectedDetails = MutableLiveData<List<Detail>>().apply { value = emptyList() }
    val selectedDetails: LiveData<List<Detail>> = _selectedDetails

    private val _isDoneRendering = SingleLiveEvent<Unit>()
    val isDoneRendering: LiveData<Unit> = _isDoneRendering

    private val _isRendering = MutableLiveData<Boolean>().apply { value = false }
    val isRendering: LiveData<Boolean> = _isRendering

    private val _currentFilmDetail = MutableLiveData<FilmDetail>()
    val currentFilmDetail: LiveData<FilmDetail> = _currentFilmDetail

    private val _selectedDuration = MutableLiveData<Int>().apply { value = 0 }
    val selectedDuration: LiveData<Int> = _selectedDuration

    private val _encodingProgress = MutableLiveData<Int>()
    val encodingProgress: LiveData<Int> = _encodingProgress

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    fun selectDetail(detail: Detail, duration: Int): Boolean {
        if (_selectedDetails.value!!.contains(detail)) {
            if (_selectedDuration.value!! - duration >= 0) {
                removeSelectedDetail(detail, duration)
                return false
            }
        } else {
            if (duration + _selectedDuration.value!! <= VideoDurations.FILM_MAX_SECONDS) {
                addSelectedDetail(detail, duration)
                return true
            }
        }
        return false
    }

    fun addSelectedDetail(detail: Detail, duration: Int) {
        val hulp = ArrayList<Detail>(_selectedDetails.value!!)
        hulp.add(detail)
        _selectedDetails.value = hulp
        _selectedDuration.value = _selectedDuration.value?.plus(duration)
    }

    fun removeSelectedDetail(detail: Detail, duration: Int) {
        val hulp = ArrayList<Detail>(_selectedDetails.value!!)
        hulp.remove(detail)
        _selectedDetails.value = hulp
        _selectedDuration.value = _selectedDuration.value?.minus(duration)
    }

    fun setIsDoneRendering() {
        _isDoneRendering.call()
        _isRendering.postValue(false)
    }

    fun setCurrentFilmDetail(filmDetail: FilmDetail) {
        _currentFilmDetail.postValue(filmDetail)
    }

    fun startRendering() {
        _isRendering.postValue(true)
        if (_selectedDetails.value.isNullOrEmpty()) {
            // TODO: add string
            _errorMessage.value = 0
        }
        val params = CreateCinemaVideoUseCase.Params(
            _selectedDetails.value!!,
            VideoEncoder.Resolution(800, 600)
        )
        createCinemaVideoUseCase.execute(
            params,
            object : DisposableObserver<CreateCinemaVideoUseCase.Status>() {
                override fun onComplete() {
                    _isRendering.postValue(false)
                    _isDoneRendering.call()
                }

                override fun onNext(progress: CreateCinemaVideoUseCase.Status) {
                    when (progress) {
                        is InProgress -> _encodingProgress.value = progress.progress
                        is Completed -> _currentFilmDetail.value = progress.videoDetail
                    }
                }

                override fun onError(e: Throwable) {
                    // TODO: message
                    _errorMessage.value = -1
                    e.printStackTrace()
                }
            })
    }
}
