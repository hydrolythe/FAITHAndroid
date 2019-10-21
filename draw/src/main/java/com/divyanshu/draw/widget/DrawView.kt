package com.divyanshu.draw.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import com.divyanshu.draw.widget.tools.DrawingContext
import com.divyanshu.draw.widget.tools.DrawingTool
import java.io.File

/**
 * Defines how much of the View's height the background may use.
 * This is used to provide a margin around the background.
 */
const val BACKGROUND_MAX_HEIGHT_USED = 0.8

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), DrawingContext {

    /**
     * Holds all [DrawingAction]s that will be drawn when calling [onDraw].
     */
    private var _drawingActions = mutableListOf<DrawingAction>()
    val drawingActions: List<DrawingAction>
        get() = _drawingActions

    /**
     * Holds a copy of all actions that were done before [clear] was called.
     * Used to restore them when calling [undo] after a call to [clear].
     */
    private var lastDrawingActions = mutableListOf<DrawingAction>()

    /**
     * Map of all actions that have been undone using the [undo] method.
     * Used to restore them when calling [redo].
     */
    private var undoneActions = mutableListOf<DrawingAction>()

    /**
     * The currently selected [com.divyanshu.draw.widget.tools.Tool].
     * By default this is the [DrawingTool]
     */
    private var currentTool = DrawingTool(this)

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

    private val drawingListeners = mutableListOf<DrawViewListener>()

    fun addDrawViewListener(newListener: DrawViewListener) {
        drawingListeners += newListener
    }

    private fun callDrawViewListeners() {
        class SendBitMapToListeners : AsyncTask<Void?, Void?, Void?>() {
            private lateinit var bitmap: Bitmap
            override fun doInBackground(vararg params: Void?): Void? {
                bitmap = getBitmap()
                return null
            }

            override fun onPostExecute(result: Void?) {
                drawingListeners.forEach {
                    it.onDrawingChanged(bitmap)
                }
            }
        }

        SendBitMapToListeners().execute()
    }

    fun undo() {
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
        callDrawViewListeners()
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
        callDrawViewListeners()
    }

    /**
     * Returns a bitmap of what is currently drawn.
     */
    @WorkerThread
    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
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
     * Add a [DrawingAction] to the list.
     * Invalidates the View, ensuring the new action gets painted.
     */
    override fun addDrawingAction(drawingAction: DrawingAction) {
        _drawingActions.add(drawingAction)
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
        lastDrawingActions = _drawingActions.toMutableList() // copy
        // TODO: Clear current tool?
        _drawingActions.clear()
        invalidate()
        callDrawViewListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentTool.handleMotionEvent(event)

        // Check if this should be called for every tool
        callDrawViewListeners()
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
     * It will be scaled to fit this view when necessary.
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
    fun setPaintedBackground(bitmapDrawable: BitmapDrawable) {
        paintedBackground = bitmapDrawable.bitmap
    }

    /**
     * Add a drawable to the canvas
     *
     * @param x: the x-coordinate of the top-left corner
     * @param y: the y-coordinate of the top-left corner
     */
    fun addDrawable(drawable: Drawable, x: Int, y: Int) {
        drawable.bounds = Rect(x, y, x + drawable.intrinsicWidth, y + drawable.intrinsicHeight)
        addDrawingAction(MyDrawable(drawable))
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
         * This includes drawing a dot, a line, undoing and redoing previous actions.
         */
        fun onDrawingChanged(bitmap: Bitmap)
    }

    /**
     * Sets the actions to be drawn.
     * This is used to save the state in a ViewModel and restore it when the View was destroyed.
     * Not everything that's part of the UI state should be saved. The other maps containing actions are only
     * there for the undo/redo and clearCanvas functionality.
     */
    fun setActions(newPaths: MutableList<DrawingAction>) {
        _drawingActions = newPaths
        invalidate()
    }

    fun setColor(@ColorInt color: Int) {
        currentTool.setColor(color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        currentTool.setStrokeWidth(newStrokeWidth)
    }

    fun setAlpha(newAlpha: Int) {
        currentTool.setAlpha(newAlpha)
    }

    override fun clearUndoneActions() {
        undoneActions.clear()
    }
}