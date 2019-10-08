package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.view.MotionEvent

class TextTool(drawingContext: DrawingContext) : Tool(drawingContext) {
    override fun handleMotionEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_UP) {
            drawingContext.openSoftKeyboard()
        }
    }

    override fun drawCurrentAction(canvas: Canvas) {
        // TODO
    }

    override fun setColor(color: Int) {
        // TODO
    }

    override fun setStrokeWidth(strokeWidth: Float) {
        // TODO
    }

    override fun setAlpha(alpha: Int) {
        // TODO
    }
}