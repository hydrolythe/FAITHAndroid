package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class DrawEmotionViewModel : ViewModel() {

    val selectedColor = MutableLiveData<@ColorInt Int>()

    val selectedLineWidth = MutableLiveData<LineWidth>()

    val eraserSelected = MutableLiveData<Boolean>()

    val undoClicked = SingleLiveEvent<Unit>()

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

    fun undo() {
        undoClicked.call()
    }

    enum class LineWidth(val width: Float) {
        THIN(12f),
        MEDIUM(30f),
        THICK(55f)
    }

    companion object {
        const val TAG = "DrawEmotionViewModel"
    }
}