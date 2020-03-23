package be.hogent.faith.faith.backpackScreen

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.SaveBackpackExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackPhotoDetailUseCase
import be.hogent.faith.faith.backpackScreen.detailFilters.CombinedDetailFilter
import be.hogent.faith.service.usecases.backpack.DeleteBackpackDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackAudioDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackDrawingDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber

object OpenState {
    const val OPEN = 2
    const val CLOSED = 3
}

object OpenDetailType {
    const val NEW = 1
    const val EDIT = 2
}

class BackpackViewModel(
    private val saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase,
    private val saveBackpackAudioDetailUseCase: SaveBackpackAudioDetailUseCase,
    private val saveBackpackPhotoDetailUseCase: SaveBackpackPhotoDetailUseCase,
    private val saveBackpackDrawingDetailUseCase: SaveBackpackDrawingDetailUseCase,
    private val saveBackpackExternalVideoDetailUseCase: SaveBackpackExternalVideoDetailUseCase,
    private val deleteBackpackDetailUseCase: DeleteBackpackDetailUseCase,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase

) : ViewModel() {

    private var details: List<Detail> = emptyList()

    private val detailFilter = CombinedDetailFilter()

    private val searchString = MutableLiveData<String>()

    val audioFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val drawingFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val photoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val textFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val videoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val externalVideoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val filteredDetails: LiveData<List<Detail>> = MediatorLiveData<List<Detail>>().apply {
        addSource(searchString) { query ->
            detailFilter.titleFilter.searchString = query
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
    }

    private var _currentFile = MutableLiveData<Detail>()
    val currentFile: LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private fun setErrorMessage(errorMsg: Int) {
        _errorMessage.postValue(errorMsg)
    }

    private val _textSavedSuccessFully = SingleLiveEvent<Int>()
    val textDetailSavedSuccessFully: LiveData<Int> = _textSavedSuccessFully

    private val _photoSavedSuccessFully = SingleLiveEvent<Int>()
    val photoDetailSavedSuccessFully: LiveData<Int> = _photoSavedSuccessFully

    private val _drawingSavedSuccessFully = SingleLiveEvent<Int>()
    val drawingDetailSavedSuccessFully: LiveData<Int> = _drawingSavedSuccessFully

    private val _externalVideoSavedSuccessFully = SingleLiveEvent<Int>()
    val externalVideoSavedSuccessFully: LiveData<Int> = _externalVideoSavedSuccessFully

    private val _audioSavedSuccessFully = SingleLiveEvent<Int>()
    val audioDetailSavedSuccessFully: LiveData<Int> = _audioSavedSuccessFully

    private val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    private val _goToCityScreen = SingleLiveEvent<Any>()
    val goToCityScreen: LiveData<Any> = _goToCityScreen

    private val _isDetailScreenOpen = MutableLiveData<Boolean>()
    val isDetailScreenOpen: LiveData<Boolean> = _isDetailScreenOpen

    private val _isPopupMenuOpen = MutableLiveData<Int>()
    val isPopupMenuOpen: LiveData<Int> = _isPopupMenuOpen

    private val _isInEditMode = MutableLiveData<Int>()
    val isInEditMode: LiveData<Int> = _isInEditMode

    private val _showSaveDialog = SingleLiveEvent<Detail>()
    val showSaveDialog: LiveData<Detail> = _showSaveDialog

    private val _goToDetail = SingleLiveEvent<Detail>()
    val goToDetail: LiveData<Detail> = _goToDetail

    private val _openDetailType = SingleLiveEvent<Int>()
    val openDetailType: LiveData<Int> = _openDetailType

    init {
        loadDetails()
    }

    // TODO tijdelijk
    private fun loadDetails() {
        val params = GetBackPackFilesDummyUseCase.Params("")
        getBackPackFilesDummyUseCase.execute(params, GetBackPackFilesDummyUseCaseHandler())
    }

    private inner class GetBackPackFilesDummyUseCaseHandler : DisposableSubscriber<List<Detail>>() {
        override fun onNext(t: List<Detail>?) {
            if (t != null) {
                setSearchStringText("")
                details = t
            }
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
        }
    }

    private val _detailDeletedSuccessfully = SingleLiveEvent<Int>()
    val detailDeletedSuccessfully: LiveData<Int> = _detailDeletedSuccessfully

    fun initialize() {
        _isInEditMode.postValue(OpenState.CLOSED)
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

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

    fun setIsInEditMode() {
        if (isInEditMode.value == OpenState.CLOSED)
            _isInEditMode.postValue(OpenState.OPEN)
        else if (isInEditMode.value == OpenState.OPEN)
            _isInEditMode.postValue(OpenState.CLOSED)
    }

    fun changePopupMenuState() {
        if (isPopupMenuOpen.value == OpenState.CLOSED)
            _isPopupMenuOpen.postValue(OpenState.OPEN)
        else if (isPopupMenuOpen.value == OpenState.OPEN)
            _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun showSaveDialog(detail: Detail) {
        _showSaveDialog.postValue(detail)
    }

    fun saveCurrentDetail(user: User, detail: Detail) {
        when (detail) {
            is DrawingDetail -> saveDrawingDetail(user, showSaveDialog.value as DrawingDetail)
            is TextDetail -> saveTextDetail(user, showSaveDialog.value as TextDetail)
            is PhotoDetail -> savePhotoDetail(user, showSaveDialog.value as PhotoDetail)
            is AudioDetail -> saveAudioDetail(user, showSaveDialog.value as AudioDetail)
            is ExternalVideoDetail -> saveExternalVideoDetail(user, showSaveDialog.value as ExternalVideoDetail)
        }
        _currentFile.postValue(null)
    }

    fun setOpenDetailType(openDetailType: Int) {
        _openDetailType.postValue(openDetailType)
    }

    fun closePopUpMenu() {
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun setDetailScreenOpen(isOpen: Boolean) {
        _isDetailScreenOpen.postValue(isOpen)
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }

    fun goToCityScreen() {
        _goToCityScreen.call()
    }

    fun onSaveClicked(fileName: String, user: User, detail: Detail) {
        val noEmptyString = checkEmptyString(fileName)
        val notMaxCharacters = checkMaxCharacters(fileName)
        val uniqueFilename = checkUniqueFilename(fileName)
        if (noEmptyString && notMaxCharacters && uniqueFilename) {
            detail.fileName = fileName
            saveCurrentDetail(user, detail)
            _detailIsSaved.call()
        } else {
            if (!noEmptyString)
                setErrorMessage(R.string.save_detail_emptyString)
            if (!notMaxCharacters)
                setErrorMessage(R.string.save_detail_maxChar)
            if (!uniqueFilename)
                setErrorMessage(R.string.save_detail_uniqueName)
        }
    }

    private fun checkUniqueFilename(fileName: String): Boolean {
        return (details.find { e -> (e.fileName == fileName) } == null)
    }

    private fun checkMaxCharacters(fileName: String): Boolean {
        return fileName.length <= 30
    }

    private fun checkEmptyString(fileName: String): Boolean {
        return fileName.isNotEmpty() || !fileName.isBlank()
    }

    fun saveTextDetail(user: User, detail: TextDetail) {
        val params = SaveBackpackTextDetailUseCase.Params(user, detail)
        saveBackpackTextDetailUseCase.execute(params, SaveBackpackTextDetailUseCaseHandler())
    }

    private inner class SaveBackpackTextDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _textSavedSuccessFully.postValue(R.string.save_text_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }

    fun saveAudioDetail(user: User, detail: AudioDetail) {
        val params = SaveBackpackAudioDetailUseCase.Params(user, detail)
        saveBackpackAudioDetailUseCase.execute(params, SaveBackpackAudioDetailUseCaseHandler())
    }

    private inner class SaveBackpackAudioDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _audioSavedSuccessFully.postValue(R.string.save_audio_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
        }
    }

    fun savePhotoDetail(user: User, detail: PhotoDetail) {
        val params = SaveBackpackPhotoDetailUseCase.Params(user, detail)
        saveBackpackPhotoDetailUseCase.execute(params, SaveBackpackPhotoDetailUseCaseHandler())
    }

    private inner class SaveBackpackPhotoDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _photoSavedSuccessFully.postValue(R.string.save_photo_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_photo_failed)
        }
    }

    fun saveDrawingDetail(user: User, detail: DrawingDetail) {
        val params = SaveBackpackDrawingDetailUseCase.Params(user, detail)
        saveBackpackDrawingDetailUseCase.execute(params, SaveBackpackDrawingDetailUseCaseHandler())
    }

    private inner class SaveBackpackDrawingDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _drawingSavedSuccessFully.postValue(R.string.save_drawing_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_drawing_failed)
        }
    }

    fun saveExternalVideoDetail(user: User, detail: ExternalVideoDetail) {
        val params = SaveBackpackExternalVideoDetailUseCase.Params(user, detail)
        saveBackpackExternalVideoDetailUseCase.execute(params, SaveBackpackExternalVideoDetailUseCaseHandler())
    }

    private inner class SaveBackpackExternalVideoDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _externalVideoSavedSuccessFully.postValue(R.string.save_video_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_external_video_failed)
        }
    }

    fun goToDetail(detail: Detail) {
        _goToDetail.postValue(detail)
    }

    fun clearSaveDialogErrorMessage() {
        _errorMessage.postValue(null)
    }

    fun deleteDetail(detail: Detail) {
        val params = DeleteBackpackDetailUseCase.Params(detail)
        deleteBackpackDetailUseCase.execute(params, DeleteBackpackDetailUseCaseHandler())
    }

    private inner class DeleteBackpackDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _detailDeletedSuccessfully.postValue(R.string.delete_detail_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_delete_detail_failure)
        }
    }

    override fun onCleared() {
        saveBackpackTextDetailUseCase.dispose()
        saveBackpackAudioDetailUseCase.dispose()
        saveBackpackDrawingDetailUseCase.dispose()
        saveBackpackPhotoDetailUseCase.dispose()
        deleteBackpackDetailUseCase.dispose()
        super.onCleared()
    }
}