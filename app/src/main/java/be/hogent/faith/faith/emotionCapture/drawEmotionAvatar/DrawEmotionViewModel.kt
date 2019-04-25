package be.hogent.faith.faith.emotionCapture.drawEmotionAvatar

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import com.divyanshu.draw.widget.DrawView
import com.divyanshu.draw.widget.MyPath
import com.divyanshu.draw.widget.PaintOptions

/**
 * ViewModel for the [DrawEmotionAvatarFragment].
 * It mainly holds the state of the [DrawView].
 */
class DrawEmotionViewModel() : ViewModel() {

    private val _selectedColor = MutableLiveData<@ColorInt Int>()
    val selectedColor: LiveData<Int>
        get() = _selectedColor

    private val _selectedLineWidth = MutableLiveData<LineWidth>()
    val selectedLineWidth: LiveData<LineWidth>
        get() = _selectedLineWidth

    private val _undoClicked = SingleLiveEvent<Unit>()
    val undoClicked: LiveData<Unit>
        get() = _undoClicked

    /**
     * Contains all paths that have been drawn on the [DrawView].
     * This belongs here because it's part of the UI state, just like how you would save text that's already been typed.
     *
     * There is no listener/observer construction required to maintain a two-way binding between the ViewModel and the
     * View because we create the object here and it gets passed to the [DrawView]. All changes made when drawing on the
     * View are done on that same object. When the [DrawView] gets recreated, the original object remains and is passed
     * again to the new View.
     * If we'd make the paths in the [DrawView] and then push them here, an observer pattern would have been required.
     */
    private val _drawnPaths = MutableLiveData<LinkedHashMap<MyPath, PaintOptions>>()
    val drawnPaths: LiveData<LinkedHashMap<MyPath, PaintOptions>>
        get() = _drawnPaths

    init {
        resetViewModel()
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

    fun resetViewModel() {
        _drawnPaths.value = LinkedHashMap()
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value = LineWidth.MEDIUM
    }
}