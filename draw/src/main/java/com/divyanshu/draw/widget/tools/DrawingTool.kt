package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.MyPath
import com.divyanshu.draw.widget.PaintOptions
import com.divyanshu.draw.widget.Point

class DrawingTool(private val drawingContext: DrawingContext) : Tool() {

    /**
     * The starting point of the drawing Action
     */
    private val startingPoint = Point()

    /**
     * The point currently being touched
     */
    private val currentPoint = Point()

    /**
     * The path currently being drawn
     */
    private var currentPath: MyPath? = null

    /**
     * Current settings for painting the [currentPath].
     */
    private var currentPaintOptions = PaintOptions()

    override fun handleMotionEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startingPoint.x = event.x
                startingPoint.y = event.y
                actionDown(x, y)
                drawingContext.clearUndoneActions()
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }
    }

    private fun actionDown(x: Float, y: Float) {
        startNewPath()

        currentPath!!.moveTo(x, y)
        currentPoint.set(x, y)
    }

    private fun actionMove(x: Float, y: Float) {
        currentPath?.quadTo(
            currentPoint.x,
            currentPoint.y,
            (x + currentPoint.x) / 2,
            (y + currentPoint.y) / 2
        )
        currentPoint.set(x, y)
    }

    private fun actionUp() {
        currentPath?.let {
            Log.d("DrawView", "Finishing path")
            it.lineTo(currentPoint.x, currentPoint.y)

            // draw a dot on click
            if (startingPoint == currentPoint) {
                it.lineTo(currentPoint.x, currentPoint.y + 2)
                it.lineTo(currentPoint.x + 1, currentPoint.y + 2)
                it.lineTo(currentPoint.x + 1, currentPoint.y)
            }

            // Add finished path to actions
            drawingContext.addDrawingAction(it)

            // Start a new Path. We have to do this here already so changing PaintOptions after drawing a line
            // doesn't change the options for that line, but sets up the new line.
            startNewPath()
        }
    }

    /**
     * Starts a new Path with the currently chosen PaintOptions
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

    /**
     * Draw the path that's currently being drawn.
     */
    override fun drawCurrentAction(canvas: Canvas) {
        currentPath?.drawOn(canvas)
    }

    override fun setColor(@ColorInt color: Int) {
        val alphaColor = ColorUtils.setAlphaComponent(color, currentPaintOptions.alpha)
        currentPaintOptions.color = alphaColor
    }

    override fun setStrokeWidth(newStrokeWidth: Float) {
        currentPaintOptions.strokeWidth = newStrokeWidth
    }

    override fun setAlpha(alpha: Int) {
        currentPaintOptions.alpha = alpha
    }
}