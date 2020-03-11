package be.hogent.faith.faith.backpackScreen

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import java.lang.Exception
import java.lang.NullPointerException
import java.util.Locale


class BackpackViewModel(
    private val saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase,
    private val saveBackpackAudioDetailUseCase: SaveBackpackAudioDetailUseCase,
    private val saveBackpackPhotoDetailUseCase: SaveBackpackPhotoDetailUseCase,
    private val saveBackpackDrawingDetailUseCase: SaveBackpackDrawingDetailUseCase,
    private val getBackPackFilesDummyUseCase: GetBackPackFilesDummyUseCase
) : ViewModel() {

    private var _details = listOf<Detail>()

    val details: LiveData<List<Detail>>
        get() = applyFilters()

    private var filterDetailType = MutableLiveData<MutableMap<Int, Boolean>>().apply {
        postValue(
            mutableMapOf(
                AUDIO_DETAIL to false,
                TEXT_DETAIL to false,
                DRAW_DETAIL to false,
                PICTURE_DETAIL to false
            )
        )
    }
    private var filterText =
        MutableLiveData<String>().apply { value = "" } // Starts with empty rv if not initialized


    private var _currentFile = MutableLiveData<Detail>()
    val currentFile: LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

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

    private val _openAddDetailMenu = MutableLiveData<View>()
    val openAddDetailMenu: LiveData<View> = _openAddDetailMenu

    private val _goToCityScreen = SingleLiveEvent<Any>()
    val goToCityScreen: LiveData<Any> = _goToCityScreen

    private val _isDetailScreenOpen = MutableLiveData<Boolean>()
    val isDetailScreenOpen: LiveData<Boolean> = _isDetailScreenOpen

    init {
        _details = getBackPackFilesDummyUseCase.getDetails()

    }

    fun setDetailScreenOpen(isOpen: Boolean) {
        _isDetailScreenOpen.postValue(isOpen)
    }

    fun openAddDetailMenu(view: View) {
        _openAddDetailMenu.postValue(view)
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

    fun removeDetail() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    fun filterSearchBar(text: String) {
        filterText.value = text
        // To move
    }

    fun setFilters(type: Int) {
        val newFilterValues = filterDetailType.value
        newFilterValues!![type] = newFilterValues[type]!!.not()
        filterDetailType.postValue(newFilterValues)
    }

    private fun applyFilters(): LiveData<List<Detail>> {
        val typeFilterRes = Transformations.map(filterDetailType) { filter ->
            _details.filter { detail ->

                if (!filter.containsValue(true)) true else {
                    if (detail is AudioDetail && filter[AUDIO_DETAIL] == true) {
                        return@filter true
                    }
                    if (detail is DrawingDetail && filter[DRAW_DETAIL] == true) {
                        return@filter true
                    }
                    if (detail is PhotoDetail && filter[PICTURE_DETAIL] == true) {
                        return@filter true
                    }
                    if (detail is TextDetail && filter[TEXT_DETAIL] == true) {
                        return@filter true
                    }
                    return@filter false
                }
            }
        }
        val textFilterRes = Transformations.map(filterText) { filterText ->
            _details.filter { detail ->
                if (filterText.isEmpty()) return@filter true else
                    return@filter detail.uuid.toString().toLowerCase(Locale.getDefault())
                        .contains(filterText.toLowerCase(Locale.getDefault()))
            }.sortedBy { it.javaClass.canonicalName }.toMutableList()
        }

        return textFilterRes.combineWith(typeFilterRes) { textRes, typeRes ->
            val resultSet = mutableSetOf<Detail>()
            try {
                if(textRes!!.size>typeRes!!.size){
                    for(detail in textRes){
                        if(typeRes.contains(detail)){
                            resultSet.add(detail)
                        }
                    }
                }else{
                    for(detail in typeRes){
                        if(textRes.contains(detail)){
                            resultSet.add(detail)
                        }
                    }
                }

            }catch (e : NullPointerException){

            }
            return@combineWith resultSet.toList()

        }
    }
    //Method to combine 2 livedata
   private fun <T, K, R> LiveData<T>.combineWith(
        liveData: LiveData<K>,
        block: (T?, K?) -> R
    ): LiveData<R> {
        val result = MediatorLiveData<R>()
        result.addSource(this) {
            result.value = block.invoke(this.value, liveData.value)
        }
        result.addSource(liveData) {
            result.value = block.invoke(this.value, liveData.value)
        }
        return result
    }
    //For testing purposes
    fun get_details(): List<Detail>{
        return _details
    }
    fun getFilterDetailType(): MutableLiveData<MutableMap<Int,Boolean>>{
        return filterDetailType
    }
}