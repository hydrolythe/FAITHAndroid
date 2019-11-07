package be.hogent.faith.faith.details.drawing.create

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import com.divyanshu.draw.widget.DrawView
import com.divyanshu.draw.widget.tools.CanvasAction

/**
 * Base VM for both the [DrawingDetailViewModel] and the [DrawEmotionAvatarViewModel].
 */
open class DrawViewModel : ViewModel() {

    protected val _selectedColor = MutableLiveData<@ColorInt Int>()
    val selectedColor: LiveData<Int>
        get() = _selectedColor

    protected val _selectedLineWidth = MutableLiveData<LineWidth>()
    val selectedLineWidth: LiveData<LineWidth>
        get() = _selectedLineWidth

    private val _undoClicked = SingleLiveEvent<Unit>()
    val undoClicked: LiveData<Unit>
        get() = _undoClicked

    private val _textClicked = SingleLiveEvent<Unit>()
    val textClicked: LiveData<Unit>
        get() = _textClicked

    private val _eraserClicked = SingleLiveEvent<Unit>()
    val eraserClicked: LiveData<Unit>
        get() = _eraserClicked

    private val _saveClicked = SingleLiveEvent<Unit>()
    val saveClicked: LiveData<Unit>
        get() = _saveClicked

    private val _pencilClicked = SingleLiveEvent<Unit>()
    val pencilClicked: LiveData<Unit>
        get() = _pencilClicked

    internal val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    /**
     * Contains all actions that have been drawn on the [DrawView].
     * This belongs here because it's part of the UI state, just like how you would save text that's already been typed.
     *
     * There is no listener/observer construction required to maintain a two-way binding between the ViewModel and the
     * View because we create the object here and it gets passed to the [DrawView]. All changes made when drawing on the
     * View are done on that same object. When the [DrawView] gets recreated, the original object remains and is passed
     * again to the new View.
     * If we'd make the actions in the [DrawView] and then push them here, an observer pattern would have been required.
     */
    protected val _drawingActions = MutableLiveData<MutableList<CanvasAction>>()
    val drawingActions: LiveData<MutableList<CanvasAction>>
        get() = _drawingActions

    init {
        _drawingActions.value = mutableListOf()
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value =
            LineWidth.MEDIUM
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

    fun onEraserClicked() {
        _eraserClicked.call()
    }

    fun onPencilClicked() {
        _pencilClicked.call()
    }

    fun onSaveClicked() {
        // We expect the listener to create a bitmap from the DrawView and call [onBitMapAvailable]
        // afterwards.
        _saveClicked.call()
    }

    fun onTextClicked() {
        _textClicked.call()
    }

    enum class LineWidth(val width: Float) {
        THIN(12f),
        MEDIUM(30f),
        THICK(55f)
    }
}
