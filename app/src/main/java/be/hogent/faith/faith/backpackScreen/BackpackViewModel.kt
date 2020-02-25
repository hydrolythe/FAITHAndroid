package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import org.koin.core.KoinComponent

class BackpackViewModel(
    private val getBackPackFilesDummyUseCase : GetBackPackFilesDummyUseCase
) : ViewModel(), KoinComponent {


    private var _details = MutableLiveData<List<Detail>>()
    val details : LiveData<List<Detail>>
    get() = _details

    init {
        _details.postValue(getBackPackFilesDummyUseCase.getDetails())
    }

}