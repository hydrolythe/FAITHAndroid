package com.divyanshu.draw.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import androidx.core.graphics.ColorUtils

/**
 * Defines how much of the View's height the background may use.
 * This is used to provide a margin around the background.
 */
const val BACKGROUND_MAX_HEIGHT_USED = 0.8

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
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

    private var currentPath: MyPath? = null

    /**
     * Current settings for painting the [currentPath].
     */
    private var currentPaintOptions = PaintOptions()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    private var mCanvasWidth: Int = 0
    private var mCanvasHeight: Int = 0

    private var mOutlineRect: Rect? = null

    private var paintedBackground: Bitmap? = null

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

    fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, currentPaintOptions.alpha)
        currentPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha * 255) / 100
        currentPaintOptions.alpha = alpha
        setColor(currentPaintOptions.color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        currentPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
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
    fun addDrawingAction(drawingAction: DrawingAction) {
        _drawingActions.add(drawingAction)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background when one is set and the [Rect] in which it will be drawn has been calculated.
        if (paintedBackground != null && mOutlineRect != null) {
            canvas.drawBitmap(
                paintedBackground!!,
                null,
                mOutlineRect!!,
                null
            )
        }
        _drawingActions.forEach { action ->
            action.drawOn(canvas)
        }

        currentPath?.drawOn(canvas)
    }

    fun changePaint(paintOptions: PaintOptions) {
        currentPaintOptions.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        currentPaintOptions.strokeWidth = paintOptions.strokeWidth
    }

    fun clearCanvas() {
        lastDrawingActions = _drawingActions.toMutableList() // copy
        currentPath?.reset()
        _drawingActions.clear()
        invalidate()
        callDrawViewListeners()
    }

    private fun actionDown(x: Float, y: Float) {
        startNewPath()

        currentPath!!.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    private fun actionMove(x: Float, y: Float) {
        currentPath?.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        currentPath?.let {
            Log.d("DrawView", "Finishing path")
            it.lineTo(mCurX, mCurY)

            // draw a dot on click
            if (mStartX == mCurX && mStartY == mCurY) {
                it.lineTo(mCurX, mCurY + 2)
                it.lineTo(mCurX + 1, mCurY + 2)
                it.lineTo(mCurX + 1, mCurY)
            }

            // Add finished path to actions
            _drawingActions.add(it)

            // Start a new txt_login_background. We have to do this here already so changing PaintOptions after drawing a line
            // doesn't change the options for that line, but sets up the new line.
            startNewPath()

            callDrawViewListeners()
        }
    }

    /**
     * Starts a new txt_login_background with the currently chosen PaintOptions
     */
    private fun startNewPath() {
        currentPaintOptions = PaintOptions(
            currentPaintOptions.color,
            currentPaintOptions.strokeWidth,
            currentPaintOptions.alpha,
            currentPaintOptions.isEraserOn
        )
        currentPath = MyPath(currentPaintOptions)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                actionDown(x, y)
                undoneActions.clear()
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }

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
        calculateOutlineRect()
    }

    /**
     * Calculates the [Rect] in which the [paintedBackground] will be drawn.
     * The middle of the [Rect] will be in the middle of the canvas.
     * It will be scaled to fit this view when necessary.
     */
    private fun calculateOutlineRect() {
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
            mOutlineRect = Rect(
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

    fun addDrawable(drawableResourceID: Int, x: Int, y: Int) {
        val drawable = context.resources.getDrawable(drawableResourceID)
        drawable.bounds = Rect(x, y, x + drawable.intrinsicWidth, y + drawable.intrinsicHeight)
        addDrawingAction(MyDrawable(drawable))
        invalidate()
    }

    fun toggleEraser() {
        currentPaintOptions.isEraserOn = currentPaintOptions.isEraserOn.not()
        invalidate()
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
}