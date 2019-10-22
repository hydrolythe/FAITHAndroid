package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.view.KeyEvent
import android.view.MotionEvent

abstract class Tool(
    open val paint: Paint,
    protected val drawingContext: DrawingContext
) {
    /**
     * Handle one of the actions defined in [android.view.MotionEvent].
     */
    abstract fun handleMotionEvent(event: MotionEvent)

    /**
     * Handle one of the actions defined in [android.view.MotionEvent].
     */
    open fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    abstract fun drawCurrentAction(canvas: Canvas)

    abstract fun finishCurrentAction()

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