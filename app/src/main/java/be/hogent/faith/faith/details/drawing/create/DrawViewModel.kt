package be.hogent.faith.faith.details.drawing.create

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import com.divyanshu.draw.widget.DrawView
import com.divyanshu.draw.widget.tools.CanvasAction
import be.hogent.faith.R

/**
 * Base VM for both the [DrawingDetailViewModel] and the [DrawEmotionAvatarViewModel].
 */
open class DrawViewModel : ViewModel() {

    protected val _selectedColor = MutableLiveData<@ColorInt Int>()
    val selectedColor: LiveData<Int>
        get() = _selectedColor

    protected val _customColor = MutableLiveData<@ColorInt Int>()
    val customColor: LiveData<Int>
        get() = _customColor

    protected val _selectedLineWidth = MutableLiveData<LineWidth>()
    val selectedLineWidth: LiveData<LineWidth>
        get() = _selectedLineWidth

    protected val _selectedTool = MutableLiveData<Tool>()
    val selectedTool: LiveData<Tool>
        get() = _selectedTool

    private val _undoClicked = SingleLiveEvent<Unit>()
    val undoClicked: LiveData<Unit>
        get() = _undoClicked

    private val _textClicked = SingleLiveEvent<Unit>()
    val textClicked: LiveData<Unit>
        get() = _textClicked

    private val _eraserClicked = SingleLiveEvent<Unit>()
    val eraserClicked: LiveData<Unit>
        get() = _eraserClicked

    private val _restartClicked = SingleLiveEvent<Unit>()
    val restartClicked: LiveData<Unit>
        get() = _restartClicked

    private val _saveClicked = SingleLiveEvent<Unit>()
    val saveClicked: LiveData<Unit>
        get() = _saveClicked

    private val _pencilClicked = SingleLiveEvent<Unit>()
    val pencilClicked: LiveData<Unit>
        get() = _pencilClicked

    private val _customColorClicked = SingleLiveEvent<Unit>()
    val customColorClicked: LiveData<Unit>
        get() = _customColorClicked

    internal val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage


    private val _showDrawTools = MutableLiveData<Boolean>()

    val visibilityDrawTools: LiveData<Int> =
        Transformations.map<Boolean, Int>(_showDrawTools) { state ->
            if (state) View.VISIBLE else View.GONE
        }

    val visibilityTextTools: LiveData<Int> =
        Transformations.map<Boolean, Int>(_showDrawTools) { state ->
            if (state) View.GONE else View.VISIBLE
        }


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
        _showDrawTools.value = true
        _selectedTool.value = Tool.PENCIL
        _selectedColor.value = R.color.black
        _selectedLineWidth.value =
            LineWidth.MEDIUM
    }

    fun pickColor(@ColorInt color: Int) {
        _selectedColor.value = color
    }

    fun setLineWidth(width: LineWidth) {
        _selectedLineWidth.value = width
        _showDrawTools.value = false
    }

    fun undo() {
        _undoClicked.call()
    }

    fun onEraserClicked() {
        _eraserClicked.call()
        _selectedTool.value = Tool.ERASER
        _showDrawTools.value = true
    }

    fun onCustomColorClicked() {
        _customColorClicked.call()
    }

    fun setCustomColor(){

    }
    fun onRestartClicked() {
        _restartClicked.call()
    }

    fun onPencilClicked() {
        _pencilClicked.call()
        _selectedTool.value = Tool.PENCIL
        _showDrawTools.value = true
    }

    fun onSaveClicked() {
        // We expect the listener to create a bitmap from the DrawView and call [onBitMapAvailable]
        // afterwards.
        _saveClicked.call()
    }

    fun onTextClicked() {
        _textClicked.call()
        _selectedTool.value = Tool.TEXT
        _showDrawTools.value = true
    }

    enum class LineWidth(val width: Float) {
        THIN(12f),
        MEDIUM(30f),
        THICK(55f)
    }

    enum class Tool {
        PENCIL,
        TEXT,
        ERASER
    }
}
