package be.hogent.faith.faith.library.eventDetailFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.LoadTextDetailUseCase

class ViewTextDetailViewModel : ViewModel() {


    private val _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text


}
