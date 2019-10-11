package be.hogent.faith.faith.emotionCapture.drawing

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import com.divyanshu.draw.widget.DrawView
import com.divyanshu.draw.widget.tools.CanvasAction

/**
 * ViewModel for the [DrawEmotionAvatarFragment].
 * It mainly holds the state of the [DrawView].
 */
class DrawViewModel : ViewModel() {

    private val _selectedColor = MutableLiveData<@ColorInt Int>()
    val selectedColor: LiveData<Int>
        get() = _selectedColor

    private val _selectedLineWidth = MutableLiveData<LineWidth>()
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

    private val _existingDetail = MutableLiveData<DrawingDetail>()
    val existingDetail: LiveData<DrawingDetail>
        get() = _existingDetail

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
    private val _drawingActions = MutableLiveData<MutableList<CanvasAction>>()
    val drawnPaths: LiveData<MutableList<CanvasAction>>
        get() = _drawingActions

    init {
        _drawingActions.value = mutableListOf()
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value = LineWidth.MEDIUM
    }

    fun loadExistingDrawingDetail(drawingDetail: DrawingDetail) {
        _existingDetail.value = drawingDetail
        // The approach in the TextViewModel of fetching the existing text  using the UC
        // and setting it in the VM is not applicable here because we have to interact
        // directly with an Android-element (DrawView). Instead setting up the DrawView is done in the
        // [MakeDrawingFragment].
        // Saving the detail here is still necessary for passing it to the EventVM in order to
        // overwrite the existing detail.
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

    // Setting the color to white would work as well, but the colors of the brushes are linked to
    // the [_selectedColor]. Selecting the color white would mean the brushes are white, making
    // them invisible. Instead, the listeners of [eraserClicked] should set the DrawViews color
    // directly, keeping the original [selectedColor].
    fun onEraserClicked() {
        _eraserClicked.call()
    }

    fun onSaveButtonClicked() {
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
