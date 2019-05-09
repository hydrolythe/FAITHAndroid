package be.hogent.faith.faith.emotionCapture.enterText

import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.SaveTextUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableCompletableObserver

class EnterTextViewModel(
    private val event: Event,
    private val saveTextUseCase : SaveTextUseCase
):ViewModel() {

    private val _text = MutableLiveData<String>()
    val text:LiveData<String>
    get()=_text

    private val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int>
        get() = _selectedTextColor

    private val _selectedFontSize = MutableLiveData<@ColorInt Int>()
    val selectedFontSize: LiveData<Int>
        get() = _selectedFontSize

    private val _boldClicked = SingleLiveEvent<Unit>()
    val boldClicked: LiveData<Unit>
        get() = _boldClicked

    private val _italicClicked = SingleLiveEvent<Unit>()
    val italicClicked: LiveData<Unit>
        get() = _italicClicked

    private val _underlineClicked = SingleLiveEvent<Unit>()
    val underlineClicked: LiveData<Unit>
        get() = _underlineClicked


    private val _textSavedSuccessFully = SingleLiveEvent<Unit>()
    val textSavedSuccessFully: LiveData<Unit>
        get() = _textSavedSuccessFully

    /**
     * Will be updated with the latest error message when an error occurs when saving the text.
     */
    private val _textSaveFailed = MutableLiveData<String>()
    val textSaveFailed: LiveData<String>
        get() = _textSaveFailed

    init {
        _selectedTextColor.value = Color.BLACK
        _selectedFontSize.value = FontSize.NORMAL.size
    }

    fun onBoldClicked(){
        _boldClicked.call()
    }

    fun onItalicClicked(){
        _italicClicked.call()
    }

    fun onUnderlineClicked(){
        _underlineClicked.call()
    }

    fun saveText() {
        if (text.value !== "") {
            val params = SaveTextUseCase.SaveTextParams(event, text.value ?: "")
            saveTextUseCase.execute(params, SaveTextUseCaseHandler())
        }
    }

    private inner class SaveTextUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _textSavedSuccessFully.value = Unit
        }

        override fun onError(e: Throwable) {
            _textSaveFailed.postValue(e.message)
        }
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize:FontSize){
        _selectedFontSize.value = fontSize.size;
    }

    fun textChanged(text:String){
        _text.value=text
        Log.d(TAG, "html ${text}")
    }

    override fun onCleared() {
        saveTextUseCase.dispose()
        super.onCleared()
    }

    enum class FontSize(val size: Int) {
        SMALL(3),
        NORMAL(6),
        LARGE(7)
    }
}