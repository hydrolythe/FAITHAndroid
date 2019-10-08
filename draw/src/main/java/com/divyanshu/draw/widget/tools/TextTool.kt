package com.divyanshu.draw.widget.tools

import android.graphics.Canvas
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection


class TextTool(drawingContext: DrawingContext) : Tool(drawingContext) {

    private val currentText = SpannableStringBuilder()
    private val inputConnection = InputConnection(drawingContext.view, true)

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

    companion object{
        val inputConnection =
    }
    inner class InputConnection internal constructor(targetView: View, fullEditor: Boolean) :
        BaseInputConnection(targetView, fullEditor) {

        override fun getEditable(): Editable {
            return currentText
        }
    }
}