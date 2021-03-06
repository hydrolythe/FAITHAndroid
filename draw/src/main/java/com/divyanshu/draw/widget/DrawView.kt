package com.divyanshu.draw.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.DrawableCompat
import com.divyanshu.draw.widget.tools.CanvasAction
import com.divyanshu.draw.widget.tools.DrawingContext
import com.divyanshu.draw.widget.tools.Tool
import com.divyanshu.draw.widget.tools.drawing.DrawingTool
import com.divyanshu.draw.widget.tools.drawing.EraserTool
import com.divyanshu.draw.widget.tools.text.TextTool
import java.io.File

/**
 * Defines how much of the View's height the background may use.
 * This is used to provide a margin around the background.
 */
const val BACKGROUND_MAX_HEIGHT_USED = 0.8

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), DrawingContext {

    override val view: View
        get() = this

    /**
     * Holds all [CanvasAction]s that will be drawn when calling [onDraw].
     */
    private var _drawingActions = mutableListOf<CanvasAction>()
    val canvasActions: List<CanvasAction>
        get() = _drawingActions

    /**
     * Holds a copy of all actions that were done before [clear] was called.
     * Used to restore them when calling [undo] after a call to [clear].
     */
    private var lastDrawingActions = mutableListOf<CanvasAction>()

    /**
     * Map of all actions that have been undone using the [undo] method.
     * Used to restore them when calling [redo].
     */
    private var undoneActions = mutableListOf<CanvasAction>()

    private var mIsSaving = false

    private var mCanvasWidth: Int = 0
    private var mCanvasHeight: Int = 0

    /**
     * Rect in which a background will be drawn, if a background is present
     */
    private val backgroundRect = Rect()
    private val fullScreenRect = Rect()

    private var paintedBackground: Bitmap? = null

    /**
     * Load a background filling the entire screen (and possibly stretching it) or scale it to fit
     * with some whitespace around
     */
    var fullScreenBackground: Boolean = true

    private val defaultPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 30f
    }

    /**
     * The currently selected [com.divyanshu.draw.widget.tools.Tool].
     * By default this is the [DrawingTool].
     */
    private var currentTool: Tool = DrawingTool(this, defaultPaint)

    init {
        // Required to open Soft Keyboard on click
        // See https://stackoverflow.com/questions/5419766/how-to-capture-soft-keyboard-input-in-a-view
        isFocusableInTouchMode = true

        setOnKeyListener { _, keyCode, event ->
            val eventHandled = currentTool.handleKeyEvent(keyCode, event)
            if (eventHandled) {
                invalidate()
            }
            eventHandled
        }
    }

    fun undo() {
        // als texttool geselecteerd is dan nog een touchevent simuleren zodat text wordt toegevoegd als laatste drawingactie
        if (currentTool is TextTool)
            touchView(this)
        if (_drawingActions.isEmpty() && lastDrawingActions.isNotEmpty()) {
            // Last action was a call to [clearCanvas]
            _drawingActions = lastDrawingActions.toMutableList() // Copy
            lastDrawingActions.clear()
            invalidate()
            return
        }
        if (_drawingActions.isEmpty()) {
            return
        }
        val lastAction = _drawingActions.lastOrNull()

        // Remove last element
        _drawingActions.removeAt(_drawingActions.size - 1)

        if (lastAction != null) {
            undoneActions.add(lastAction)
        }

        invalidate()
    }

    fun redo() {
        if (undoneActions.isEmpty()) {
            return
        }

        with(undoneActions.last()) {
            addDrawingAction(this)
            undoneActions.remove(this)
        }

        invalidate()
    }

    /**
     * Returns a bitmap of what is currently drawn.
     */
    @WorkerThread
    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    /**
     * Returns a bitmap of what is currently drawn.
     */
    @WorkerThread
    fun getBitmap(saveListener: (bitmap: Bitmap) -> Unit) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        saveListener(bitmap)
    }

    /**
     * Add a [CanvasAction] to the list.
     * Invalidates the View, ensuring the new action gets painted.
     */
    override fun addDrawingAction(action: CanvasAction) {
        _drawingActions.add(action)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)

        _drawingActions.forEach { it.drawOn(canvas) }

        currentTool.drawCurrentAction(canvas)
    }

    /**
     * Draw the background when one is set and the [Rect] in which it will be drawn has been calculated.
     */
    private fun drawBackground(canvas: Canvas) {
        if (paintedBackground != null) {
            canvas.drawBitmap(
                paintedBackground!!,
                null,
                if (fullScreenBackground) fullScreenRect else backgroundRect,
                null
            )
        }
    }

    fun clearCanvas() {
        // Save drawing actions so we can undo the clear
        lastDrawingActions = _drawingActions.toMutableList()

        _drawingActions.clear()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentTool.handleMotionEvent(event)

        invalidate()
        return true
    }

    /**
     * Listener for when the size of this View changes.
     * When a View is initially constructed, its width and height are both 0.
     * Once the View has been (re)measured, this method is called.
     *
     * Used here to save the actual width and height of the view and
     * calculate the outline of the background accordingly.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasHeight = h
        mCanvasWidth = w
        calculateBackGroundRect()
        calculateFullScreenRect()
    }

    private fun calculateFullScreenRect() {
        fullScreenRect.set(0, 0, mCanvasWidth, mCanvasHeight)
    }

    /**
     * Calculates the [Rect] in which the [paintedBackground] will be drawn.
     * The middle of the [Rect] will be in the middle of the canvas.
     * The rect will be scaled to fit this view when necessary.
     */
    private fun calculateBackGroundRect() {
        paintedBackground?.let {
            val maxsize = (mCanvasHeight * BACKGROUND_MAX_HEIGHT_USED).toInt()
            val outWidth: Int
            val outHeight: Int
            if (it.width > it.height) {
                outWidth = maxsize
                outHeight = it.height * maxsize / it.width
            } else {
                outHeight = maxsize
                outWidth = it.width * maxsize / it.height
            }
            backgroundRect.set(
                middleOfCanvas().x - (outWidth) / 2,
                middleOfCanvas().y - (outHeight) / 2,
                middleOfCanvas().x + (outWidth) / 2,
                middleOfCanvas().y + (outHeight) / 2
            )
        }
    }

    private fun middleOfCanvas(): Point {
        return Point(mCanvasWidth / 2, mCanvasHeight / 2)
    }

    /**
     * Sets a drawable as the background of this View.
     * It won't be the set as the normal [View.getBackground], but it will be painted as the bottommost layer on the
     * View's canvas.
     * This is done to make sure both the background and the actual drawing are saved together (and are aligned)
     * when saving the drawing using [getBitmap].
     *
     * The background will be placed in the middle of the view, and possibly scaled to fit.
     */
    fun setPaintedBackground(vectorDrawable: Drawable) {
        paintedBackground = getBitmapFromVectorDrawable(vectorDrawable)
        calculateBackGroundRect()
    }

    /**
     * Add a drawable to a specific location on the canvas.
     * @param x the leftmost point of the drawable
     * @param y the topmost point of the drawable
     */
    fun addDrawable(drawableResourceID: Int, x: Int, y: Int) {
        val drawable = context.resources.getDrawable(drawableResourceID)
        drawable.bounds = Rect(x, y, x + drawable.intrinsicWidth, y + drawable.intrinsicHeight)
        addDrawingAction(DrawableAction(drawable))
        invalidate()
    }

    /**
     * @see [setPaintedBackground]
     */
    fun setImageBackground(imageFile: File) {
        val drawableImage = BitmapDrawable.createFromPath(imageFile.path) as BitmapDrawable
        drawableImage.bounds = Rect(0, 0, mCanvasWidth, mCanvasHeight)
        setPaintedBackground(drawableImage)
    }

    interface DrawViewListener {
        /**
         * Called every time after the user has performed an action on the drawing, changing it in the process.
         */
        fun onDrawingChanged(bitmap: Bitmap)
    }

    /**
     * Sets the actions to be drawn.
     */
    // This is used to save the state in a ViewModel and restore it when the View was destroyed.
    // Not everything that's part of the UI state should be saved. The other maps containing actions are only
    // there for the undo/redo and clearCanvas functionality.
    fun setActions(newActions: MutableList<CanvasAction>) {
        _drawingActions = newActions
        invalidate()
    }

    fun setColor(@ColorInt color: Int) {
        if (currentTool is EraserTool)
            pickDrawingTool()
        currentTool.setColor(color)
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        currentTool.setStrokeWidth(strokeWidth)
        invalidate()
    }

    fun setAlpha(alpha: Int) {
        currentTool.setAlpha(alpha)
        invalidate()
    }

    override fun clearUndoneActions() {
        undoneActions.clear()
    }

    fun pickDrawingTool() {
        currentTool.finishCurrentAction()
        val paintToUse = Paint(currentTool.paint)
        currentTool = DrawingTool(this, paintToUse)
    }

    fun pickEraserTool(alpha: Int) {
        currentTool.finishCurrentAction()
        val paintToUse = Paint(currentTool.paint)
        currentTool = EraserTool(this, paintToUse, alpha)
    }

    fun pickTextTool() {
        currentTool.finishCurrentAction()
        val paintToUse = Paint(currentTool.paint)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentTool = TextTool(this, paintToUse, imm)
    }

    // simuleren van een touchevent
    private fun touchView(view: View) {
        view.dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP,
                0.0f,
                0.0f,
                0
            )
        )
    }

    private fun getBitmapFromVectorDrawable(vectorDrawable: Drawable): Bitmap? {
        var drawable = vectorDrawable
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(vectorDrawable).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}