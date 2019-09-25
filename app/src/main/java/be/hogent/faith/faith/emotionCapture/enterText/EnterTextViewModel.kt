package be.hogent.faith.faith.emotionCapture.enterText

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.LoadTextDetailUseCase
import io.reactivex.observers.DisposableSingleObserver

class EnterTextViewModel(private val loadTextDetailUseCase: LoadTextDetailUseCase) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    private val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int>
        get() = _selectedTextColor

    private val _selectedFontSize = MutableLiveData<FontSize>()
    val selectedFontSize: LiveData<FontSize>
        get() = _selectedFontSize

    private val _boldClicked = MutableLiveData<Boolean?>()
    val boldClicked: LiveData<Boolean?>
        get() = _boldClicked

    private val _italicClicked = MutableLiveData<Boolean?>()
    val italicClicked: LiveData<Boolean?>
        get() = _italicClicked

    private val _underlineClicked = MutableLiveData<Boolean?>()
    val underlineClicked: LiveData<Boolean?>
        get() = _underlineClicked

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    init {
        _selectedTextColor.value = Color.BLACK
        _selectedFontSize.value = FontSize.NORMAL
    }

    fun onBoldClicked() {
        _boldClicked.value = !(boldClicked.value ?: false)
    }

    fun onItalicClicked() {
        _italicClicked.value = !(_italicClicked.value ?: false)
    }

    fun onUnderlineClicked() {
        _underlineClicked.value = !(_underlineClicked.value ?: false)
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize: FontSize) {
        _selectedFontSize.value = fontSize
    }

    fun setText(text: String) {
        _text.value = text
    }

    fun loadExistingTextDetail(textDetail: TextDetail) {
        val params = LoadTextDetailUseCase.LoadTextParams(textDetail)
        loadTextDetailUseCase.execute(params, LoadTextUseCaseHandler())
    }

    enum class FontSize(val size: Int) {
        SMALL(3),
        NORMAL(6),
        LARGE(7)
    }

    private inner class LoadTextUseCaseHandler : DisposableSingleObserver<String>() {
        override fun onSuccess(loadedString: String) {
            _text.value = loadedString
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }
}
