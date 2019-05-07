package be.hogent.faith.faith.emotionCapture.enterText

import android.util.Log
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent

class EnterTextViewModel(
    private val event: Event
):ViewModel() {

    private val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int>
        get() = _selectedTextColor

    private val _boldClicked = SingleLiveEvent<Unit>()
    val boldClicked: LiveData<Unit>
        get() = _boldClicked

    private val _italicClicked = SingleLiveEvent<Unit>()
    val italicClicked: LiveData<Unit>
        get() = _italicClicked

    private val _underlineClicked = SingleLiveEvent<Unit>()
    val underlineClicked: LiveData<Unit>
        get() = _underlineClicked

    fun onBoldClicked(){
        _boldClicked.call()
    }

    fun onItalicClicked(){
        _italicClicked.call()
    }

    fun onUnderlineClicked(){
        _underlineClicked.call()
    }

    fun onSaveClicked(){
        Log.d("Enter text ", "onsaveclicked")
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

}