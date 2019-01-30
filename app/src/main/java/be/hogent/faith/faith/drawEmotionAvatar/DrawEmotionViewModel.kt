package be.hogent.faith.faith.drawEmotionAvatar

import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawEmotionViewModel : ViewModel() {

    val selectedColor = MutableLiveData<@ColorInt Int>()

    val selectedLineWidth = MutableLiveData<LineWidth>()

    val eraserSelected = MutableLiveData<Boolean>()

    companion object {
        const val TAG = "DrawEmotionViewModel"
    }

    fun pickColor(@ColorInt color: Int) {
        selectedColor.value = color
    }

    fun setLineWidth(width: LineWidth) {
        selectedLineWidth.value = width
    }

    fun toggleEraser() {
        eraserSelected.value = eraserSelected.value?.not()

    }

    enum class LineWidth(val width: Float) {
        THIN(8f),
        MEDIUM(12f),
        THICK(15f)
    }
}