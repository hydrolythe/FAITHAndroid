package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

/**
 * ViewModel for the [DrawEmotionAvatarFragment].
 */
class DrawEmotionViewModel : ViewModel() {

    private val _selectedColor = MutableLiveData<@ColorInt Int>()
    val selectedColor: LiveData<Int>
        get() = _selectedColor

    private val _selectedLineWidth = MutableLiveData<LineWidth>()
    val selectedLineWidth: LiveData<LineWidth>
        get() = _selectedLineWidth

    private val _eraserSelected = MutableLiveData<Boolean>()

    private val _undoClicked = SingleLiveEvent<Unit>()
    val undoClicked: LiveData<Unit>
        get() = _undoClicked

    init {
        _eraserSelected.value = false
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value = LineWidth.MEDIUM
    }

    fun pickColor(@ColorInt color: Int) {
        _selectedColor.value = color
    }

    fun setLineWidth(width: LineWidth) {
        _selectedLineWidth.value = width
    }

    fun undo() {
        _undoClicked.call()
    }

    enum class LineWidth(val width: Float) {
        THIN(12f),
        MEDIUM(30f),
        THICK(55f)
    }
}