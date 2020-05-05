package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableObserver
import java.io.File
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

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    init {
        loadDetails()
    }

    private fun loadDetails() {
        val params = GetBackPackDataUseCase.Params()
        getBackPackDataUseCase.execute(params, object : DisposableObserver<List<Detail>>() {

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_load_backpack)
            }

            override fun onNext(t: List<Detail>) {
                t.let {
                    details = it
                    setSearchStringText("")
                }
            }
        })
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
