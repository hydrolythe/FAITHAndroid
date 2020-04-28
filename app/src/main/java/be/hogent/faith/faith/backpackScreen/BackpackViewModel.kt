package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesUseCase
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
    private val getBackPackFilesUseCase: GetBackPackFilesUseCase
) : DetailsContainerViewModel<Backpack>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    loadDetailFileUseCase,
    backpack
) {

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    init {
        loadDetails()
    }

    // TODO tijdelijk
    private fun loadDetails() {
        val params = GetBackPackFilesUseCase.Params("")
        getBackPackFilesUseCase.execute(params, GetBackPackFilesDummyUseCaseHandler())
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

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
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
}

/**
 * Holds the state of the edit mode: open or closed
 */
enum class EditModeState { OPEN, CLOSED }
