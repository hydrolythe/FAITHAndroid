package be.hogent.faith.faith.backpackScreen

import android.view.View
import android.widget.Toast
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
import be.hogent.faith.faith.backpackScreen.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackAudioDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackDrawingDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackPhotoDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver

object OpenState {
    const val OPEN = 2
    const val CLOSED = 3
}


class BackpackViewModel(
    private val saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase,
    private val saveBackpackAudioDetailUseCase: SaveBackpackAudioDetailUseCase,
    private val saveBackpackPhotoDetailUseCase: SaveBackpackPhotoDetailUseCase,
    private val saveBackpackDrawingDetailUseCase: SaveBackpackDrawingDetailUseCase,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase
) : ViewModel() {

    private var _details = MutableLiveData<List<Detail>>()
    val details: LiveData<List<Detail>>
        get() = _details

    private var _currentFile = MutableLiveData<Detail>()
    val currentFile: LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

    private var filteredDetails = mutableListOf<Detail>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _textSavedSuccessFully = SingleLiveEvent<Int>()
    val textDetailSavedSuccessFully: LiveData<Int> = _textSavedSuccessFully

    private val _photoSavedSuccessFully = SingleLiveEvent<Int>()
    val photoDetailSavedSuccessFully: LiveData<Int> = _photoSavedSuccessFully

    private val _drawingSavedSuccessFully = SingleLiveEvent<Int>()
    val drawingDetailSavedSuccessFully: LiveData<Int> = _drawingSavedSuccessFully

    private val _audioSavedSuccessFully = SingleLiveEvent<Int>()
    val audioDetailSavedSuccessFully: LiveData<Int> = _audioSavedSuccessFully

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    private val _onAddClicked = MutableLiveData<View>()
    val onAddClicked : LiveData<View> = _onAddClicked

    private val _goToCityScreen = SingleLiveEvent<Any>()
    val goToCityScreen: LiveData<Any> = _goToCityScreen

    private val _isDetailScreenOpen = MutableLiveData<Boolean>()
    val isDetailScreenOpen: LiveData<Boolean> = _isDetailScreenOpen

    private val _isPopupMenuOpen = MutableLiveData<Int>()
    val isPopupMenuOpen : LiveData<Int> = _isPopupMenuOpen

    private val _isInEditMode = MutableLiveData<Int>()
    val isInEditMode : LiveData<Int> = _isInEditMode

    private val _showSaveDialog = MutableLiveData<Detail>()
    val showSaveDialog: LiveData<Detail> = _showSaveDialog

    init {
        _details.postValue(getBackPackFilesDummyUseCase.getDetails())
    }

    fun initialize(){
        _isInEditMode.postValue(OpenState.CLOSED)
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun setIsInEditMode(){
        if(isInEditMode.value == OpenState.CLOSED)
            _isInEditMode.postValue(OpenState.OPEN)
        else if(isInEditMode.value == OpenState.OPEN)
            _isInEditMode.postValue(OpenState.CLOSED)
    }

    fun changePopupMenuState(){
        if(isPopupMenuOpen.value == OpenState.CLOSED)
            _isPopupMenuOpen.postValue(OpenState.OPEN)
        else if(isPopupMenuOpen.value == OpenState.OPEN)
            _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun showSaveDialog(detail: Detail){
        _showSaveDialog.postValue(detail)
    }

    fun saveCurrentDetail(detail : Detail){
        when (detail) {
            is DrawingDetail -> saveDrawingDetail(showSaveDialog.value as DrawingDetail)
            is TextDetail -> saveTextDetail(showSaveDialog.value as TextDetail)
            is PhotoDetail -> savePhotoDetail(showSaveDialog.value as PhotoDetail)
            is AudioDetail -> saveAudioDetail(showSaveDialog.value as AudioDetail)
        }
    }

    fun closePopUpMenu(){
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun setDetailScreenOpen(isOpen: Boolean) {
        _isDetailScreenOpen.postValue(isOpen)
        _isPopupMenuOpen.postValue(OpenState.CLOSED)
    }

    fun setOnAddClicked(view: View) {
        _onAddClicked.postValue(view)
    }

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }

    fun goToCityScreen() {
        _goToCityScreen.call()
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

    fun saveAudioDetail(detail: AudioDetail) {
        val params = SaveBackpackAudioDetailUseCase.Params(detail)
        saveBackpackAudioDetailUseCase.execute(params, SaveBackpackAudioDetailUseCaseHandler())
    }

    private inner class SaveBackpackAudioDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _audioSavedSuccessFully.postValue(R.string.save_audio_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
        }
    }

    fun savePhotoDetail(detail: PhotoDetail) {
        val params = SaveBackpackPhotoDetailUseCase.Params(detail)
        saveBackpackPhotoDetailUseCase.execute(params, SaveBackpackPhotoDetailUseCaseHandler())
    }

    private inner class SaveBackpackPhotoDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _photoSavedSuccessFully.postValue(R.string.save_photo_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_photo_failed)
        }
    }

    fun saveDrawingDetail(detail: DrawingDetail) {
        val params = SaveBackpackDrawingDetailUseCase.Params(detail)
        saveBackpackDrawingDetailUseCase.execute(params, SaveBackpackDrawingDetailUseCaseHandler())
    }

    private inner class SaveBackpackDrawingDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _drawingSavedSuccessFully.postValue(R.string.save_drawing_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_drawing_failed)
        }
    }

    override fun onCleared() {
        saveBackpackTextDetailUseCase.dispose()
        saveBackpackAudioDetailUseCase.dispose()
        saveBackpackDrawingDetailUseCase.dispose()
        saveBackpackPhotoDetailUseCase.dispose()
        super.onCleared()
    }

    fun filterSearchBar(text: String): List<Detail> {
        // TODO update for detail.title

        if (text.isEmpty()) {
            return _details.value!!
        } else {
            filteredDetails = mutableListOf()
            for (detail in _details.value!!) {
                if (detail.uuid.toString().toLowerCase().contains(text.toLowerCase())) {
                    filteredDetails.add(detail)
                }
            }
        }
        return filteredDetails
    }

    fun filterType(type: Int): List<Detail> {

        when (type) {
            AUDIO_DETAIL -> if (filteredDetails == _details.value!!) filteredDetails =
                _details.value!!.filterIsInstance<AudioDetail>()
                    .toMutableList() else filteredDetails = _details.value!!.toMutableList()
            DRAW_DETAIL -> if (filteredDetails == _details.value!!) filteredDetails =
                _details.value!!.filterIsInstance<DrawingDetail>()
                    .toMutableList() else filteredDetails = _details.value!!.toMutableList()
            TEXT_DETAIL -> if (filteredDetails == _details.value!!) filteredDetails =
                _details.value!!.filterIsInstance<TextDetail>()
                    .toMutableList() else filteredDetails = _details.value!!.toMutableList()
            PICTURE_DETAIL -> if (filteredDetails == _details.value!!) filteredDetails =
                _details.value!!.filterIsInstance<PhotoDetail>()
                    .toMutableList() else filteredDetails = _details.value!!.toMutableList()
        }
        return filteredDetails
    }
}