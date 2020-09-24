package be.hogent.faith.faith.backpack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase

class BackpackViewModel(
    saveBackpackDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>,
    deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>,
    backpack: Backpack,
    loadDetailFileUseCase: LoadDetailFileUseCase<Backpack>,
    getBackPackDataUseCase: GetDetailsContainerDataUseCase<Backpack>
) : DetailsContainerViewModel<Backpack>(
    saveBackpackDetailUseCase,
    deleteBackpackDetailUseCase,
    loadDetailFileUseCase,
    getBackPackDataUseCase,
    backpack
) {

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }
}
