package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveYoutubeDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber
import java.util.Date

object OpenState {
    const val OPEN = 2
    const val CLOSED = 3
}

object OpenDetailType {
    const val NEW = 1
    const val EDIT = 2
}

class BackpackViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase,
    private val saveYoutubeDetailUseCase: SaveYoutubeDetailUseCase
) : DetailsContainerViewModel(saveBackpackDetailUseCase, deleteBackpackDetailUseCase) {

    private val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    private val _isDetailScreenOpen = MutableLiveData<Boolean>()
    val isDetailScreenOpen: LiveData<Boolean> = _isDetailScreenOpen

    private val _isPopupMenuOpen = MutableLiveData<Int>()
    val isPopupMenuOpen: LiveData<Int> = _isPopupMenuOpen

    private val _isInEditMode = MutableLiveData<Int>()
    val isInEditMode: LiveData<Int> = _isInEditMode

    private val _showSaveDialog = SingleLiveEvent<Detail>()
    val showSaveDialog: LiveData<Detail> = _showSaveDialog

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

    fun initialize() {
        _isInEditMode.postValue(OpenState.CLOSED)
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
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
            is ExternalVideoDetail -> saveExternalVideoDetail(
                user,
                showSaveDialog.value as ExternalVideoDetail
            )
            is YoutubeVideoDetail -> saveYoutubeDetail(user, detail)
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

    /**
     * YouTube detail isn't stored in a file and saved differently and it's specific to the backpack
     * that's why this isn't in the detailcontainer viewmodel
     */
    fun saveYoutubeDetail(user: User, detail: YoutubeVideoDetail) {
        val params = SaveYoutubeDetailUseCase.Params(user, detail)
        saveYoutubeDetailUseCase.execute(params, SaveBackpackYoutubeDetailUseCaseHandler())
    }

    private inner class SaveBackpackYoutubeDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _infoMessage.postValue(R.string.save_video_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_external_video_failed)
        }
    }

    fun saveYoutubeVideoDetail(fileName: String, user: User, detail: Detail) {
        val notMaxCharacters = checkMaxCharacters(fileName)
        val uniqueFilename = checkUniqueFilename(fileName)

        if (!notMaxCharacters)
            detail.fileName = fileName.substring(0, 29)

        if (!uniqueFilename) {
            detail.fileName = detail.fileName + " (" + Date() + ")"
        }
        saveCurrentDetail(user, detail)
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

    fun clearSaveDialogErrorMessage() {
        _errorMessage.postValue(null)
    }
}