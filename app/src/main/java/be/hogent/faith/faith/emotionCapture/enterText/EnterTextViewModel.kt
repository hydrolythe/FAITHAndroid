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

    init {
        _selectedTextColor.value = Color.BLACK
        _selectedFontSize.value = FontSize.NORMAL.size
    }

    fun onBoldClicked() {
        _boldClicked.call()
    }

    fun onItalicClicked() {
        _italicClicked.call()
    }

    fun onUnderlineClicked() {
        _underlineClicked.call()
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize: FontSize) {
        _selectedFontSize.value = fontSize.size
    }

    fun textChanged(text: String) {
        _text.value = text
        Log.d(TAG, "html $text")
    }

    enum class FontSize(val size: Int) {
        SMALL(3),
        NORMAL(6),
        LARGE(7)
    }
}