package be.hogent.faith.faith.detailscontainer

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.detailscontainer.detailFilters.CombinedDetailFilter
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

abstract class DetailsContainerViewModel<T : DetailsContainer>(
    private val saveDetailsContainerDetailUseCase: SaveDetailsContainerDetailUseCase<T>,
    private val deleteDetailsContainerDetailUseCase: DeleteDetailsContainerDetailUseCase<T>,
    private val loadDetailFileUseCase: LoadDetailFileUseCase<T>,
    protected val detailsContainer: T
) : ViewModel() {

    protected var details: List<Detail> = detailsContainer.details

    // filter
    private val detailFilter = CombinedDetailFilter()

    val searchString = MutableLiveData<String>()

    private val _startDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.MIN.plusDays(1)
    }
    val startDate: LiveData<LocalDate>
        get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.now()
    }

    val endDate: LiveData<LocalDate>
        get() = _endDate

    val audioFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }
    val drawingFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }
    val photoFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }
    val textFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }
    val videoFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }
    val externalVideoFilterEnabled = MutableLiveData<Boolean>().apply { this.value = false }

    val deleteEnabled = MutableLiveData<Boolean>().apply { this.value = false }

    private var _dateRangeClicked = SingleLiveEvent<Unit>()
    val dateRangeClicked: LiveData<Unit> = _dateRangeClicked

    val dateRangeString: LiveData<String> = MediatorLiveData<String>().apply {
        this.addSource(startDate) { value = combineLatestDates(startDate, endDate) }
        this.addSource(endDate) { value = combineLatestDates(startDate, endDate) }
    }

    val filteredDetails: LiveData<List<Detail>> = MediatorLiveData<List<Detail>>().apply {
        addSource(searchString) { query ->
            detailFilter.titleFilter.searchString = query
            value = detailFilter.filter(details)
        }
        addSource(startDate) { startDate ->
            detailFilter.dateFilter.startDate = startDate
            value = detailFilter.filter(details)
        }
        addSource(endDate) { endDate ->
            detailFilter.dateFilter.endDate = endDate
            value = detailFilter.filter(details)
        }
        addSource(drawingFilterEnabled) { enabled ->
            detailFilter.hasDrawingDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(textFilterEnabled) { enabled ->
            detailFilter.hasTextDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(photoFilterEnabled) { enabled ->
            detailFilter.hasPhotoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(audioFilterEnabled) { enabled ->
            detailFilter.hasAudioDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(externalVideoFilterEnabled) { enabled ->
            detailFilter.hasExternalVideoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(videoFilterEnabled) { enabled ->
            detailFilter.hasVideoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
    }

    /**
     *  opening and saving (first showSaveDialog, then  ,  a detail
     */
    protected var _currentFile = MutableLiveData<Detail>()
    val currentFile: LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

    fun setCurrentFileAndLoadCorrespongFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

    // nieuw detail aanmaken of bestaand detail openen?
    protected val _openDetailMode = SingleLiveEvent<OpenDetailMode>()
    val openDetailMode: LiveData<OpenDetailMode> = _openDetailMode

    // ga naar een bestaand detail
    protected val _goToDetail = SingleLiveEvent<Detail>()
    val goToDetail: LiveData<Detail> = _goToDetail

    // detail is opgeslaan
    protected val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    // detail moet worden opgeslaan, maar eerst dient de titel,... te worden opgevraagd via SaveDialog
    private val _showSaveDialog = SingleLiveEvent<Detail>()
    val showSaveDialog: LiveData<Detail> = _showSaveDialog

    /**
     * title and date of a new detail
     */
    val detailTitle = MutableLiveData<String>()
    protected val _detailTitleErrorMessage = MutableLiveData<Int>()
    val detailTitleErrorMessage: LiveData<Int>
        get() = _detailTitleErrorMessage

    val detailDate = MutableLiveData<LocalDateTime>()
    val detailDateString: LiveData<String> =
        Transformations.map(detailDate) { date ->
            date.format(DateTimeFormatter.ISO_DATE)
        }
    private val _detailDateButtonClicked = SingleLiveEvent<Unit>()
    val detailDateButtonClicked: LiveData<Unit> = _detailDateButtonClicked

    fun onDateButtonClicked() {
        _detailDateButtonClicked.call()
    }

    fun resetDetails() {
        detailTitle.value = ""
        detailDate.value = LocalDateTime.now()
        _detailTitleErrorMessage.postValue(null)
    }

    /**
     * errormessages and information
     */
    protected val _errorMessage = SingleLiveEvent<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    protected fun setErrorMessage(errorMsg: Int) {
        _errorMessage.postValue(errorMsg)
    }

    protected val _infoMessage = SingleLiveEvent<Int>()
    val infoMessage: LiveData<Int> = _infoMessage

    protected val _goToCityScreen = SingleLiveEvent<Any>()
    val goToCityScreen: LiveData<Any> = _goToCityScreen

    fun onFilterPhotosClicked() {
        photoFilterEnabled.value = photoFilterEnabled.value!!.not()
    }

    fun onFilterTextClicked() {
        textFilterEnabled.value = textFilterEnabled.value!!.not()
    }

    fun onFilterDrawingClicked() {
        drawingFilterEnabled.value = drawingFilterEnabled.value!!.not()
    }

    fun onFilterAudioClicked() {
        audioFilterEnabled.value = audioFilterEnabled.value!!.not()
    }

    fun onFilterVideoClicked() {
        videoFilterEnabled.value = videoFilterEnabled.value!!.not()
    }

    fun onFilterExternalVideoClicked() {
        externalVideoFilterEnabled.value = externalVideoFilterEnabled.value!!.not()
    }

    fun setSearchStringText(text: String) {
        searchString.postValue(text)
    }

    fun setDateRange(startDate: Long?, endDate: Long?) {
        _startDate.value = startDate?.let { toLocalDate(it) } ?: LocalDate.MIN.plusDays(1)
        _endDate.value = endDate?.let { toLocalDate(it) } ?: LocalDate.now()
    }

    private fun combineLatestDates(
        startDate: LiveData<LocalDate>,
        endDate: LiveData<LocalDate>
    ): String {
        val van =
            if (startDate.value == LocalDate.MIN.plusDays(1)) "van" else startDate.value!!.format(
                DateTimeFormatter.ISO_DATE
            )
        val tot = endDate.value!!.format(DateTimeFormatter.ISO_DATE)
        return "$van - $tot"
    }

    fun onDateRangeClicked() {
        _dateRangeClicked.call()
    }

    fun onDeleteClicked() {
        deleteEnabled.value = deleteEnabled.value!!.not()
    }

    fun goToCityScreen() {
        _goToCityScreen.call()
    }

    fun setOpenDetailType(openDetailMode: OpenDetailMode) {
        _openDetailMode.postValue(openDetailMode)
    }

    // detail is aangemaakt, maar titel,... moet nog worden opgevraagd
    fun showSaveDialog(detail: Detail) {
        _showSaveDialog.postValue(detail)
    }

    // opslaan van detail
    open fun saveCurrentDetail(user: User, detail: Detail) {
        when (detail) {
            is DrawingDetail -> saveDrawingDetail(user, showSaveDialog.value as DrawingDetail)
            is TextDetail -> saveTextDetail(user, showSaveDialog.value as TextDetail)
            is PhotoDetail -> savePhotoDetail(user, showSaveDialog.value as PhotoDetail)
            is AudioDetail -> saveAudioDetail(user, showSaveDialog.value as AudioDetail)
            is ExternalVideoDetail -> saveExternalVideoDetail(
                user,
                showSaveDialog.value as ExternalVideoDetail
            )
            is YoutubeVideoDetail -> saveYoutubeDetail(user, detail)
        }
        _currentFile.postValue(null)
    }

    fun getCurrentDetailFile(detail: Detail) {
        val params = LoadDetailFileUseCase.Params(detail, detailsContainer)
        loadDetailFileUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _currentFile.postValue(detail)
                _goToDetail.value = detail
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_load_detail_failed)
            }
        })
    }

    fun onSaveClicked(title: String, user: User, detail: Detail, date: LocalDateTime = LocalDateTime.now()) {
        val notMaxCharacters = checkMaxCharacters(title)
        val uniqueTitle = checkUniqueTitle(title)
        if (title.isNotEmpty() && notMaxCharacters && uniqueTitle) {
            detail.title = title
            detail.dateTime = date
            _detailTitleErrorMessage.value = R.string.empty
            saveCurrentDetail(user, detail)
        } else {
            if (title.isBlank())
                _detailTitleErrorMessage.postValue(R.string.save_detail_emptyString)
            if (!notMaxCharacters)
                _detailTitleErrorMessage.postValue(R.string.save_detail_maxChar)
            if (!uniqueTitle)
                _detailTitleErrorMessage.postValue(R.string.save_detail_uniqueName)
        }
    }

    fun saveTextDetail(user: User, detail: TextDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, detailsContainer, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_text_success)
                _detailIsSaved.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_text_failed)
            }
        })
    }

    fun saveAudioDetail(user: User, detail: AudioDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, detailsContainer, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_audio_success)
                _detailIsSaved.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_audio_failed)
            }
        })
    }

    fun savePhotoDetail(user: User, detail: PhotoDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, detailsContainer, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_photo_success)
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_photo_failed)
            }
        })
    }

    fun saveDrawingDetail(user: User, detail: DrawingDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, detailsContainer, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_drawing_success)
                _detailIsSaved.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_drawing_failed)
            }
        })
    }

    fun saveExternalVideoDetail(user: User, detail: ExternalVideoDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, detailsContainer, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_video_success)
                _detailIsSaved.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_external_video_failed)
            }
        })
    }

    // Youtube videos are only in the Backpack, so this is not in the [DetailsContainerViewModel].
    fun saveYoutubeDetail(user: User, detail: YoutubeVideoDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_video_success)
                _detailIsSaved.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_external_video_failed)
            }
        })
    }

    fun goToDetail(detail: Detail) {
        _goToDetail.postValue(detail)
    }

    fun deleteDetail(detail: Detail) {
        val params = DeleteDetailsContainerDetailUseCase.Params(detail, detailsContainer)
        deleteDetailsContainerDetailUseCase.execute(params, DeleteDetailUseCaseHandler())
    }

    private inner class DeleteDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _infoMessage.postValue(R.string.delete_detail_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_delete_detail_failure)
        }
    }

    override fun onCleared() {
        saveDetailsContainerDetailUseCase.dispose()
        deleteDetailsContainerDetailUseCase.dispose()
        super.onCleared()
    }

    private fun toLocalDate(milliseconds: Long): LocalDate? {
        return Instant.ofEpochMilli(milliseconds) // Convert count-of-milliseconds-since-epoch into a date-time in UTC (`Instant`).
            .atZone(ZoneId.of("Europe/Brussels")) // Adjust into the wall-clock time used by the people of a particular region (a time zone). Produces a `ZonedDateTime` object.
            .toLocalDate()
    }

    protected fun checkUniqueTitle(title: String): Boolean {
        return (details.find { e -> (e.title == title) } == null)
    }

    protected fun checkMaxCharacters(title: String): Boolean {
        return title.length <= 30
    }
}

/**
 * If a detail is new --> savedialog is shown
 * When editing an existing detail --> savedialog isn't shown
 */
enum class OpenDetailMode { NEW, EDIT }
