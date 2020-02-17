package be.hogent.faith.faith.details.drawing.create

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
 * Base VM for both the [DrawingDetailViewModel]
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

    private val _redoClicked = SingleLiveEvent<Unit>()
    val redoClicked: LiveData<Unit>
        get() = _redoClicked

    private val _pencilClicked = SingleLiveEvent<Unit>()
    val pencilClicked: LiveData<Unit>
        get() = _pencilClicked

    private val _textClicked = SingleLiveEvent<Unit>()
    val textClicked: LiveData<Unit>
        get() = _textClicked

    private val _eraserClicked = SingleLiveEvent<Unit>()
    val eraserClicked: LiveData<Unit>
        get() = _eraserClicked

    private val _customColorClicked = SingleLiveEvent<Unit>()
    val customColorClicked: LiveData<Unit>
        get() = _customColorClicked

    private val _restartClicked = SingleLiveEvent<Unit>()
    val restartClicked: LiveData<Unit>
        get() = _restartClicked

    private val _saveClicked = SingleLiveEvent<Unit>()
    val saveClicked: LiveData<Unit>
        get() = _saveClicked

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    internal val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    val customColorSelected: LiveData<Boolean> =
        Transformations.map<Int, Boolean>(_selectedColor) { color ->
            color == _customColor.value
        }

    private val _showColorPicker = MutableLiveData<Boolean>()
    val showColorPicker: LiveData<Boolean>
        get() = _showColorPicker

    private val _showColorWidthTools = MutableLiveData<Boolean>()
    val showColorWidthTools: LiveData<Boolean>
        get() = _showColorWidthTools

    private var _lastToolclicked: Tool? = null

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
        _showColorPicker.value = false
        _showColorWidthTools.value = false
        _selectedTool.value = Tool.PENCIL
        _selectedColor.value = R.color.black
        _customColor.value = R.color.green
        _selectedLineWidth.value =
            LineWidth.MEDIUM
    }

    fun pickColor(@ColorInt color: Int) {
        _selectedColor.value = color
        // als op eraser en je kiest ander kleur dan moet je terug naar penseel anders gom je in dat kleur
        if (_selectedTool.value == Tool.ERASER)
            onPencilClicked()
        _showColorPicker.value = false
    }

    fun setCustomColor(@ColorInt color: Int) {
        _customColor.value = color
        _selectedColor.value = color
        // als op eraser en je kiest ander kleur dan moet je terug naar penseel anders gom je in dat kleur
    }

    fun setLineWidth(width: LineWidth) {
        _selectedLineWidth.value = width
        if (_selectedTool.value != Tool.ERASER) _selectedColor.value = _selectedColor.value // anders past de tint zich niet aan
    }

    fun undo() {
        _undoClicked.call()
    }

    fun redo() {
        _redoClicked.call()
    }

    fun onCustomColorClicked() {
        _customColorClicked.call()
        _selectedColor.value = _customColor.value
        _showColorPicker.value = true
        if (_selectedTool.value == Tool.ERASER)
            onPencilClicked()
    }

    fun onRestartClicked() {
        _restartClicked.call()
    }

    fun onPencilClicked() {
        setSelectedTool(Tool.PENCIL)
        _pencilClicked.call()
    }

    fun onTextClicked() {
        setSelectedTool(Tool.TEXT)
        _textClicked.call()
    }

    fun onEraserClicked() {
        setSelectedTool(Tool.ERASER)
        _eraserClicked.call()
        _showColorPicker.value = false
    }

    private fun setSelectedTool(tool: Tool) {
        if (tool == _selectedTool.value)
            _showColorWidthTools.value = !_showColorWidthTools.value!!
        else
            _showColorWidthTools.value = true
        _selectedTool.value = tool
    }

    fun onSaveClicked() {
        // We expect the listener to create a bitmap from the DrawView and call [onBitMapAvailable]
        // afterwards.
        _saveClicked.call()
    }

    fun onCancelClicked() {
        _cancelClicked.call()
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
