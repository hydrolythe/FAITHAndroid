package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawEmotionViewModel : ViewModel() {

    val selectedColor = MutableLiveData<@ColorInt Int>()

    val selectedLineWidth = MutableLiveData<LineWidth>()

    val eraserSelected = MutableLiveData<Boolean>()

    init {
        eraserSelected.value = false
        selectedColor.value = Color.BLACK
        selectedLineWidth.value = LineWidth.MEDIUM
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

    companion object {
        const val TAG = "DrawEmotionViewModel"
    }
}