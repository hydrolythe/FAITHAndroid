package com.divyanshu.draw.widget.tools.text

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.divyanshu.draw.widget.Point
import com.divyanshu.draw.widget.tools.DrawingContext
import com.divyanshu.draw.widget.tools.Tool

class TextTool(
    drawingContext: DrawingContext,
    paint: Paint,
    private val imm: InputMethodManager
) : Tool(paint, drawingContext) {
    private var currentTextAction: TextAction? = null

    private var textPaint = TextPaint(paint).apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        textSize = paint.strokeWidth * drawingContext.view.resources.displayMetrics.density
    }

    override fun handleMotionEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_UP) {
            // Store the current [TextAction] and create a new one when user clicks somewhere else
            if (otherLocationClicked(event)) {
                finishCurrentAction()
            }
            // We have to create a new TextPaint every time so changes in Paint don't propagate to
            // other TextActions (as they would all share the same reference)
            textPaint = TextPaint(textPaint)
            currentTextAction =
                TextAction(Point(event.x, event.y), textPaint)
            showSoftKeyboard()
        }
    }

    private fun otherLocationClicked(event: MotionEvent): Boolean {
        // At the start there's no [currentTextAction] so we didn't click another location
        if (currentTextAction == null) {
            return false
        } else {
            return (event.x != currentTextAction?.position?.x || event.y != currentTextAction?.position?.y)
        }
    }

    override fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.unicodeChar == 0) { // control character
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    currentTextAction?.removeLastCharacter()
                    return true
                }
            } else { // text character
                currentTextAction?.addCharacter(event.unicodeChar.toChar())
                return true
            }
        }
        return false
    }

    private fun showSoftKeyboard() {
        drawingContext.view.requestFocus()
        imm.showSoftInput(drawingContext.view, 0)
    }

    override fun drawCurrentAction(canvas: Canvas) {
        currentTextAction?.drawOn(canvas)
    }

    override fun setColor(color: Int) {
        super.setColor(color)
        textPaint.color = color
    }

    override fun setAlpha(alpha: Int) {
        super.setAlpha(alpha)
        textPaint.alpha = alpha
    }

    override fun setStrokeWidth(strokeWidth: Float) {
        textPaint.textSize = strokeWidth * drawingContext.view.resources.displayMetrics.density
    }

    override fun finishCurrentAction() {
        // If we switch tools before starting the action the [currentTextAction] will still be null.
        if (currentTextAction != null) {
            drawingContext.addDrawingAction(currentTextAction!!)
        }
    }
}