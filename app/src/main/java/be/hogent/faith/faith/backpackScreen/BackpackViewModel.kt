package be.hogent.faith.faith.backpackScreen

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import org.koin.core.KoinComponent

class BackpackViewModel(
    private val saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase,
    private val getBackPackFilesDummyUseCase : GetBackPackFilesDummyUseCase
) : ViewModel(), KoinComponent {


    private var _details = MutableLiveData<List<Detail>>()
    val details : LiveData<List<Detail>>
    get() = _details

    private var _currentFile = MutableLiveData<Detail>()
    val currentFile : LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail : Detail){
        _currentFile.postValue(detail)
    }

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _textSavedSuccessFully = SingleLiveEvent<Int>()
    val textDetailSavedSuccessFully: LiveData<Int> = _textSavedSuccessFully

    init {
        _details.postValue(getBackPackFilesDummyUseCase.getDetails())
    }

    fun saveTextDetail(detail: TextDetail) {
        val params = SaveBackpackTextDetailUseCase.Params(detail)
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

    fun saveAudioDetail(detail: AudioDetail){

    }

    fun savePhotoDetail(detail: PhotoDetail){

    }

    fun saveDrawingDetail(detail: DrawingDetail){

    }

    override fun onCleared() {
        saveBackpackTextDetailUseCase.dispose()
        super.onCleared()
    }

}