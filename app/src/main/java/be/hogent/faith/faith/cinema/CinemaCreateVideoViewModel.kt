package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import java.io.File

class CinemaCreateVideoViewModel : ViewModel() {

    private val _selectedDetails = MutableLiveData<List<Detail>>()
    val selectedDetails: LiveData<List<Detail>> = _selectedDetails

    private val _isDoneRendering = SingleLiveEvent<Unit>()
    val isDoneRendering: LiveData<Unit> = _isDoneRendering

    private val _isRendering = MutableLiveData<Boolean>()
    val isRendering: LiveData<Boolean> = _isRendering

    private val _currentFilmDetail = MutableLiveData<FilmDetail>()
    val currentFilmDetail: LiveData<FilmDetail> = _currentFilmDetail

    private val _selectedDuration = MutableLiveData<Int>()
    val selectedDuration: LiveData<Int> = _selectedDuration

    init {
        _selectedDetails.postValue(emptyList())
        _isRendering.postValue(false)
        _selectedDuration.postValue(0)
    }

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

    fun setIsRendering() {
        _isRendering.postValue(true)
    }

    fun setCurrentFilmDetail(filmDetail: FilmDetail) {
        _currentFilmDetail.postValue(filmDetail)
    }
}
