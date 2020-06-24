package be.hogent.faith.faith.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status.Completed
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status.InProgress
import be.hogent.faith.service.usecases.cinema.VideoEncoder
import io.reactivex.rxjava3.observers.DisposableObserver
import org.threeten.bp.LocalDateTime
import timber.log.Timber

/**
 * Allows for the creation of [FilmDetail]s that will be added to the user's movies ("Mijn filmpjes")
 * This uses the DetailViewModel structure that is normally used details to a DetailContainer,
 * but here, in the last step, it is added to the movies in the cinema, not to its details.
 */
class CinemaCreateVideoViewModel(
    private val createCinemaVideoUseCase: CreateCinemaVideoUseCase
) : ViewModel(), DetailViewModel<FilmDetail> {

    var cinema: Cinema? = null

    private val _selectedDetails = MutableLiveData<List<Detail>>().apply { value = emptyList() }
    val selectedDetails: LiveData<List<Detail>> = _selectedDetails

    private val _isRendering = MutableLiveData<Boolean>().apply { value = false }
    val isRendering: LiveData<Boolean> = _isRendering

    private var currentDetail: FilmDetail? = null

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

    override fun onSaveClicked() {
        if (_selectedDetails.value.isNullOrEmpty()) {
            _errorMessage.value = R.string.error_cinema_no_sources_selected
            return
        }
        _isRendering.postValue(true)
        val params = CreateCinemaVideoUseCase.Params(
            _selectedDetails.value!!,
            cinema!!,
            VideoEncoder.Resolution(800, 600)
        )
        createCinemaVideoUseCase.execute(
            params,
            object : DisposableObserver<CreateCinemaVideoUseCase.Status>() {
                override fun onComplete() {
                    _isRendering.postValue(false)
                    _getDetailMetaData.call()
                }

                override fun onNext(progress: CreateCinemaVideoUseCase.Status) {
                    when (progress) {
                        is InProgress -> _encodingProgress.value = progress.progress
                        is Completed -> currentDetail = progress.videoDetail
                    }
                }

                override fun onError(e: Throwable) {
                    _errorMessage.value = R.string.encode_film_error
                    Timber.e(e)
                }
            })
    }

    private val _savedDetail = MutableLiveData<FilmDetail>()
    override val savedDetail: LiveData<FilmDetail> = _savedDetail

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    override fun loadExistingDetail(existingDetail: FilmDetail) {
        throw NotImplementedError()
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        currentDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = currentDetail
    }
}
