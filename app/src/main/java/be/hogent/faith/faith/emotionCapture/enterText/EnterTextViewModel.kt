package be.hogent.faith.faith.emotionCapture.enterText

import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.util.TAG

class EnterTextViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    private val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int>
        get() = _selectedTextColor

    private val _selectedFontSize = MutableLiveData<FontSize>()
    val selectedFontSize: LiveData<FontSize>
        get() = _selectedFontSize

    private val _boldClicked = MutableLiveData<Boolean>()
    val boldClicked: LiveData<Boolean>
        get() = _boldClicked

    private val _italicClicked = MutableLiveData<Boolean>()
    val italicClicked: LiveData<Boolean>
        get() = _italicClicked

    private val _underlineClicked = MutableLiveData<Boolean>()
    val underlineClicked: LiveData<Boolean>
        get() = _underlineClicked

    init {
        _selectedTextColor.value = Color.BLACK
        _selectedFontSize.value = FontSize.NORMAL
        //anders staat bold in het begin aan. De editor registreert een click aan bold aan, volgende click bold uit
        _boldClicked.value = true
        _boldClicked.value = false
        _italicClicked.value=true
        _italicClicked.value=false
        _underlineClicked.value = true
        _underlineClicked.value=false
    }

    fun onBoldClicked() {
        _boldClicked.value = !boldClicked.value!!
    }

    fun onItalicClicked() {
        _italicClicked.value = !_italicClicked.value!!
    }

    fun onUnderlineClicked() {
        _underlineClicked.value = !_underlineClicked.value!!
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize: FontSize) {
        _selectedFontSize.value = fontSize
    }

    fun textChanged(text: String) {
        _text.value = text
    }

    enum class FontSize(val size: Int) {
        SMALL(3),
        NORMAL(6),
        LARGE(7)
    }
}