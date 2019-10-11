package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

abstract class Tool(
    val paint: Paint,
    protected val drawingContext: DrawingContext
) {
    /**
     * Handle one of the actions defined in [android.view.MotionEvent].
     */
    abstract fun handleMotionEvent(event: MotionEvent)

    abstract fun drawCurrentAction(canvas: Canvas)

    open fun setColor(color: Int) {
        paint.color = color
    }

    open fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    open fun setStrokeWidth(strokeWidth: Float) {
        paint.strokeWidth = strokeWidth
    }

}