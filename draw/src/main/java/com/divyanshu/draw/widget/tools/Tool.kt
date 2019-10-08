package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.view.MotionEvent
import androidx.annotation.ColorInt

abstract class Tool {
    /**
     * Handle one of the actions defined in [android.view.MotionEvent].
     */
    abstract fun handleMotionEvent(event: MotionEvent)

    abstract fun drawCurrentAction(canvas: Canvas)

    abstract fun setColor(@ColorInt color: Int)

    abstract fun setStrokeWidth(strokeWidth: Float)

    abstract fun setAlpha(alpha: Int)
}