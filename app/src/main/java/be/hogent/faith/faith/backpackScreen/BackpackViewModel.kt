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
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.subscribers.DisposableSubscriber

class BackpackViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase
) : DetailsContainerViewModel(saveBackpackDetailUseCase, deleteBackpackDetailUseCase) {

    private val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    private val _isInEditMode = MutableLiveData<EditModeState>()
    val isInEditMode: LiveData<EditModeState> = _isInEditMode

    private val _showSaveDialog = SingleLiveEvent<Detail>()
    val showSaveDialog: LiveData<Detail> = _showSaveDialog

    private val _openDetailMode = SingleLiveEvent<OpenDetailMode>()
    val openDetailMode: LiveData<OpenDetailMode> = _openDetailMode

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
        _isInEditMode.postValue(EditModeState.CLOSED)
    }

    fun setIsInEditMode() {
        if (isInEditMode.value == EditModeState.CLOSED)
            _isInEditMode.postValue(EditModeState.OPEN)
        else if (isInEditMode.value == EditModeState.OPEN)
            _isInEditMode.postValue(EditModeState.CLOSED)
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
        }
        _currentFile.postValue(null)
    }

    fun setOpenDetailType(openDetailMode: OpenDetailMode) {
        _openDetailMode.postValue(openDetailMode)
    }

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }

    fun onSaveClicked(title: String, user: User, detail: Detail) {
        val notMaxCharacters = checkMaxCharacters(title)
        val uniqueFilename = checkUniqueFilename(title)
        if (title.isNotEmpty() && notMaxCharacters && uniqueFilename) {
            detail.title = title

            saveCurrentDetail(user, detail)
            _detailIsSaved.call()
        } else {
            if (title.isBlank())
                setErrorMessage(R.string.save_detail_emptyString)
            if (!notMaxCharacters)
                setErrorMessage(R.string.save_detail_maxChar)
            if (!uniqueFilename)
                setErrorMessage(R.string.save_detail_uniqueName)
        }
    }

    private fun checkUniqueFilename(title: String): Boolean {
        return (details.find { e -> (e.title == title) } == null)
    }

    private fun checkMaxCharacters(title: String): Boolean {
        return title.length <= 30
    }

    fun clearSaveDialogErrorMessage() {
        _errorMessage.postValue(null)
    }
}

/**
 * Holds the state of the edit mode: open or closed
 */
enum class EditModeState { OPEN, CLOSED }

/**
 * If a detail is new --> savedialog is shown
 * When editing an existing detail --> savedialog isn't shown
 */
enum class OpenDetailMode { NEW, EDIT }
