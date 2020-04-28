package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.GetBackPackDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.subscribers.DisposableSubscriber
import java.util.Date

class BackpackViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>,
    backpack: Backpack,
    loadDetailFileUseCase: LoadDetailFileUseCase<Backpack>,
    private val getBackPackDataUseCase: GetBackPackDataUseCase
) : DetailsContainerViewModel<Backpack>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    loadDetailFileUseCase,
    backpack
) {

    private val _detailIsSaved = SingleLiveEvent<Any>()
    val detailIsSaved: LiveData<Any> = _detailIsSaved

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    init {
        loadDetails()
    }

    private fun loadDetails() {
        val params = GetBackPackDataUseCase.Params()
        getBackPackDataUseCase.execute(params, object : DisposableSubscriber<List<Detail>>() {
            override fun onNext(loadedDetails: List<Detail>?) {
                loadedDetails?.let {
                    details = it
                    setSearchStringText("")
                }
            }

            override fun onComplete() {}

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_load_backpack)
            }
        })
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
