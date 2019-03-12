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
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Defines how much of the View's height the background may use.
 * This is used to provide a margin around the background.
 */
const val BACKGROUND_MAX_HEIGHT_USED = 0.8

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaths = LinkedHashMap<MyPath, PaintOptions>()

    private var mLastPaths = LinkedHashMap<MyPath, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<MyPath, PaintOptions>()

    private var mPaint = Paint()
    private var mPath = MyPath()
    private var mPaintOptions = PaintOptions()

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

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    fun addDrawViewListener(newListener: DrawViewListener) {
        drawingListeners += newListener
    }

    private fun callDrawViewListeners() {
        Log.i("DrawView", "Drawing was updated, calling ${drawingListeners.size} listeners")
        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = getBitmap()
            Log.i("DrawView", "Bitmap size: ${bitmap.width}, ${bitmap.height}")
            drawingListeners.forEach {
                it.onDrawingChanged(bitmap)
            }
        }
    }

    fun undo() {
        if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedHashMap<MyPath, PaintOptions>
            mLastPaths.clear()
            invalidate()
            return
        }
        if (mPaths.isEmpty()) {
            return
        }
        val lastPath = mPaths.values.lastOrNull()
        val lastKey = mPaths.keys.lastOrNull()

        mPaths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            mUndonePaths[lastKey] = lastPath
        }
        invalidate()
        callDrawViewListeners()
    }

    fun redo() {
        if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        addPath(lastKey, mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()
        callDrawViewListeners()
    }

    fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha * 255) / 100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    /**
     * Returns a bitmap of what is currently drawn.
     */
    @WorkerThread
    fun getBitmap(): Bitmap {
        Log.i("DrawView", "View size: $width, $height")
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    fun addPath(path: MyPath, options: PaintOptions) {
        mPaths[path] = options
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
        for ((key, value) in mPaths) {
            changePaint(value)
            canvas.drawPath(key, mPaint)
        }

        changePaint(mPaintOptions)
        canvas.drawPath(mPath, mPaint)
    }

    fun changePaint(paintOptions: PaintOptions) {
        mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth
    }

    fun clearCanvas() {
        mLastPaths = mPaths.clone() as LinkedHashMap<MyPath, PaintOptions>
        mPath.reset()
        mPaths.clear()
        invalidate()
        callDrawViewListeners()
    }

    private fun actionDown(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    private fun actionMove(x: Float, y: Float) {
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }

        mPaths[mPath] = mPaintOptions
        mPath = MyPath()
        mPaintOptions = PaintOptions(
            mPaintOptions.color,
            mPaintOptions.strokeWidth,
            mPaintOptions.alpha,
            mPaintOptions.isEraserOn
        )
        callDrawViewListeners()
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
                mUndonePaths.clear()
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

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
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
     * Sets the paths to be drawn.
     * This is used to save the state in a ViewModel and restore it when the View was destroyed.
     * Not everything that's part of the UI state should be saved. The other maps containing baths are only
     * there for the undo/redo and clearCanvas functionality which we *currently* don't fully use. (Only undo)
     */
    fun setPaths(newPaths: LinkedHashMap<MyPath, PaintOptions>) {
        mPaths = newPaths
        invalidate()
    }
}