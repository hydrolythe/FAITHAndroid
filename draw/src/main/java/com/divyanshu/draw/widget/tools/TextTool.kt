package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection


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