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
import be.hogent.faith.service.usecases.backpack.DeleteBackpackDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveYoutubeDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber
import java.util.Date

class BackpackViewModel(
    saveBackpackDetailUseCase: SaveBackpackDetailUseCase,
    deleteBackpackDetailUseCase: DeleteBackpackDetailUseCase,
    backpack: Backpack,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase,
    private val saveYoutubeDetailUseCase: SaveYoutubeDetailUseCase
) : DetailsContainerViewModel<Backpack>(saveBackpackDetailUseCase, deleteBackpackDetailUseCase, backpack) {

    private val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

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

    fun setOpenDetailType(openDetailMode: OpenDetailMode) {
        _openDetailMode.postValue(openDetailMode)
    }

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }

    fun onSaveClicked(title: String, user: User, detail: Detail) {
        val notMaxCharacters = checkMaxCharacters(title)
        val uniqueTitle = checkUniqueTitle(title)
        if (title.isNotEmpty() && notMaxCharacters && uniqueTitle) {
            detail.title = title

            saveCurrentDetail(user, detail)
            _detailIsSaved.call()
        } else {
            if (title.isBlank())
                setErrorMessage(R.string.save_detail_emptyString)
            if (!notMaxCharacters)
                setErrorMessage(R.string.save_detail_maxChar)
            if (!uniqueTitle)
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

    fun saveYoutubeVideoDetail(title: String, user: User, detail: Detail) {
        val notMaxCharacters = checkMaxCharacters(title)
        val uniqueFilename = checkUniqueTitle(title)

        if (!notMaxCharacters)
            detail.title = title.substring(0, 29)

        if (!uniqueFilename) {
            detail.title = detail.title + " (" + Date() + ")"
        }
        saveCurrentDetail(user, detail)
    }

    private fun checkUniqueTitle(title: String): Boolean {
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
