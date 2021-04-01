package be.hogent.faith.faith.detailscontainer

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Environment
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import be.hogent.faith.R
import be.hogent.faith.faith.detailscontainer.detailFilters.CombinedDetailFilter
import be.hogent.faith.faith.models.DetailsContainer
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.DetailType
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.detail.VideoDetail
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import com.google.api.client.util.IOUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


abstract class DetailsContainerViewModel<T : DetailsContainer>(
    val detailsContainer: T,
    val detailsContainerRepository: IDetailsContainerRepository,
    val context: Context
) : LoadingViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    protected open val details: List<Detail>
        get() = detailsContainer.details

    private val detailFilter = CombinedDetailFilter()

    val searchString = MutableLiveData<String>()

    protected val _startDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.MIN.plusDays(1)
    }
    val startDate: LiveData<LocalDate>
        get() = _startDate

    protected val _endDate = MutableLiveData<LocalDate>().apply {
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

    private var _dateRangeClicked = SingleLiveEvent<Unit>()
    val dateRangeClicked: LiveData<Unit> = _dateRangeClicked

    val dateRangeString: LiveData<String> = MediatorLiveData<String>().apply {
        this.addSource(startDate) { value = combineLatestDates(startDate, endDate) }
        this.addSource(endDate) { value = combineLatestDates(startDate, endDate) }
    }

    private val _addButtonClicked = SingleLiveEvent<Unit>()
    val addButtonClicked: LiveData<Unit> = _addButtonClicked

    val detailsPresent: LiveData<Boolean> by lazy { Transformations.map(filteredDetails) { detailsList -> detailsList.isNotEmpty() } }

    val filteredDetails: LiveData<List<Detail>> = MediatorLiveData<List<Detail>>().apply {
        addSource(searchString) { query ->
            detailFilter.titleFilter.searchString = query
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(startDate) { startDate ->
            detailFilter.dateFilter.startDate = startDate
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(endDate) { endDate ->
            detailFilter.dateFilter.endDate = endDate
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(drawingFilterEnabled) { enabled ->
            detailFilter.hasDrawingDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(textFilterEnabled) { enabled ->
            detailFilter.hasTextDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(photoFilterEnabled) { enabled ->
            detailFilter.hasPhotoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(audioFilterEnabled) { enabled ->
            detailFilter.hasAudioDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(externalVideoFilterEnabled) { enabled ->
            detailFilter.hasVideoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
        addSource(videoFilterEnabled) { enabled ->
            detailFilter.hasYoutubeDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details).sortedBy { it.dateTime }.reversed()
        }
    }

    /**
     * Indicates that the user is currently deleting items from the container.
     * (Indicated by the x's being shown next to each detail.
     */
    val deleteModeEnabled = MutableLiveData<Boolean>().apply { this.value = false }

    val deleteButtonVisible: LiveData<Boolean> =
        Transformations.map(filteredDetails) { list -> list.isNotEmpty() }

    /**
     *  opening and saving (first showSaveDialog, then  ,  a detail
     */
    protected var _currentDetail = MutableLiveData<Detail>()
    val currentDetail: LiveData<Detail>
        get() = _currentDetail

    fun setCurrentFile(detail: Detail?) {
        _currentDetail.postValue(detail)
    }

    fun setCurrentFileAndLoadCorrespondingFile(detail: Detail) {
        getCurrentDetailFile(detail)
    }

    // nieuw detail aanmaken of bestaand detail openen?
    protected val _openDetailMode = SingleLiveEvent<OpenDetailMode>()
    val openDetailMode: LiveData<OpenDetailMode> = _openDetailMode

    protected val _goToDetail = SingleLiveEvent<Detail>()
    val goToDetail: LiveData<Detail> = _goToDetail

    protected val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

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

    private var oldFiles = mutableMapOf<File,File>()


    init {
        loadDetails()
    }

    private fun loadDetails() {
        startLoading()
        uiScope.launch {
            val result = detailsContainerRepository.loadDetails()
            if (result.success != null) {
                detailsContainer.setDetails(result.success.details)
                setSearchStringText("")
                doneLoading()
            }
            if (result.exception != null) {
                Timber.e(result.exception)
                _errorMessage.postValue(R.string.error_load_backpack)
                doneLoading()
            }
        }
    }

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

    protected fun forceDetailsUpdate() {
        audioFilterEnabled.value = audioFilterEnabled.value
    }

    fun onAddButtonClicked() {
        _addButtonClicked.call()
    }

    private fun combineLatestDates(
        startDate: LiveData<LocalDate>,
        endDate: LiveData<LocalDate>
    ): String {
        val from =
            if (startDate.value == LocalDate.MIN.plusDays(1)) "van" else startDate.value!!.format(
                DateTimeFormatter.ofPattern("dd,MMM yyyy")
            )
        val to = endDate.value!!.format(DateTimeFormatter.ofPattern("dd,MMM yyyy"))
        return "$from - $to"
    }

    fun onDateRangeClicked() {
        _dateRangeClicked.call()
    }

    fun onDeleteClicked() {
        deleteModeEnabled.value = deleteModeEnabled.value!!.not()
    }

    fun goToCityScreen() {
        _goToCityScreen.call()
    }

    fun setOpenDetailType(openDetailMode: OpenDetailMode) {
        _openDetailMode.postValue(openDetailMode)
    }

    fun resetDateRange() {
        _startDate.value = LocalDate.MIN.plusDays(1)
        _endDate.value = LocalDate.now()
    }

    // opslaan van detail
    open fun saveCurrentDetail(user: User, detail: Detail) {
        when (detail) {
            is DrawingDetail -> saveDrawingDetail(user, detail)
            is TextDetail -> saveTextDetail(user, detail)
            is PhotoDetail -> savePhotoDetail(user, detail)
            is AudioDetail -> saveAudioDetail(user, detail)
            is VideoDetail -> saveVideoDetail(user, detail)
            is YoutubeVideoDetail -> saveYoutubeDetail(user, detail)
        }
        _currentDetail.postValue(null)
    }

    fun getCurrentDetailFile(detail: Detail) {
        ioScope.launch {
            val wrapper = ContextWrapper(context)
            var path = Environment.getExternalStorageDirectory()
            var file = Environment.getExternalStorageDirectory()
            val title = detail.title
            when (detail) {
                is PhotoDetail -> {
                    path =
                        wrapper.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE)
                    file = File(path, "$title.jpg")
                }
                is DrawingDetail -> {
                    path =
                        wrapper.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE)
                    file = File(path, "$title.png")
                }
                is AudioDetail -> {
                    path = wrapper.getDir(Environment.DIRECTORY_MUSIC, Context.MODE_PRIVATE)
                    file = File(path, "$title.mpeg")
                }
            }
            if (file.absolutePath.equals(detail.file.absolutePath)|| detail is TextDetail) {
                _currentDetail.postValue(detail)
                _goToDetail.postValue(detail)
            } else {
                val rewrittenFile = detail.file.absolutePath.replace("_encrypted", "")
                val stream = detailsContainerRepository.downloadFile(File(rewrittenFile))
                if (stream.success != null) {
                    try {
                        path.mkdir()
                        val inputStream = stream.success.byteStream()
                        val outputStream = FileOutputStream(file)
                        IOUtils.copy(inputStream, outputStream)
                        oldFiles.put(file,detail.file)
                        detail.file = file
                        _currentDetail.postValue(detail)
                        _goToDetail.postValue(detail)
                    } catch (e: Exception) {
                        Timber.e(e)
                        _errorMessage.postValue(R.string.error_load_detail_failed)
                    }
                }
                if (stream.exception != null) {
                    _errorMessage.postValue(R.string.error_load_detail_failed)
                }
            }
        }
    }

    fun saveTextDetail(user: User, detail: TextDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.TEXT
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_text_success)
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_text_failed)
                doneLoading()
            }
        }
    }

    fun saveAudioDetail(user: User, detail: AudioDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.AUDIO
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_audio_success)
                forceDetailsUpdate()
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_audio_failed)
                doneLoading()
            }
        }
    }

    fun savePhotoDetail(user: User, detail: PhotoDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.PHOTO
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_photo_success)
                forceDetailsUpdate()
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_photo_failed)
                doneLoading()
            }
        }
    }

    fun saveDrawingDetail(user: User, detail: DrawingDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.DRAWING
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_drawing_success)
                forceDetailsUpdate()
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_drawing_failed)
                doneLoading()
            }
        }
    }

    fun saveVideoDetail(user: User, detail: VideoDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.VIDEO
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_video_success)
                forceDetailsUpdate()
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_external_video_failed)
                doneLoading()
            }
        }
    }

    fun saveYoutubeDetail(user: User, detail: YoutubeVideoDetail) {
        startLoading()
        val changedDetail = ExpandedDetail(
            detail.file, detail.title, detail.uuid, detail.dateTime, detail.thumbnail,
            DetailType.YOUTUBE
        )
        uiScope.launch {
            val result = detailsContainerRepository.saveDetail(user, changedDetail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.save_video_success)
                forceDetailsUpdate()
                _detailIsSaved.call()
                doneLoading()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_external_video_failed)
                doneLoading()
            }
        }
    }

    fun goToDetail(detail: Detail) {
        _goToDetail.postValue(detail)
    }

    fun deleteDetail(detail: Detail) {
        uiScope.launch {
            if(oldFiles.containsKey(detail.file)){
                detail.file = oldFiles[detail.file]!!
            }
            val result = detailsContainerRepository.deleteDetail(detail)
            if (result.success != null) {
                _infoMessage.postValue(R.string.delete_detail_success)
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_delete_detail_failure)
            }
        }
    }

    private fun toLocalDate(milliseconds: Long): LocalDate? {
        return Instant.ofEpochMilli(milliseconds) // Convert count-of-milliseconds-since-epoch into a date-time in UTC (`Instant`).
            .atZone(ZoneId.of("Europe/Brussels")) // Adjust into the wall-clock time used by the people of a particular region (a time zone). Produces a `ZonedDateTime` object.
            .toLocalDate()
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(R.string.empty)
    }
}

/**
 * If a detail is new --> savedialog is shown
 * When editing an existing detail --> savedialog isn't shown
 */
enum class OpenDetailMode { NEW, EDIT, VIEW }
