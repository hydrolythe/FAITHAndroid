package com.divyanshu.draw.widget.tools.text

import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableStringBuilder
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

    private val textPaint = TextPaint(paint).apply {
        isAntiAlias = true
        strokeWidth = 13f
        textSize = paint.strokeWidth * drawingContext.view.resources.displayMetrics.density
    }

    override fun handleMotionEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_UP) {
            // Store the current [TextAction] and create a new one when user clicks somewhere else
            if (otherLocationClicked(event)) {
                drawingContext.addDrawingAction(currentTextAction!!)
            }
            currentTextAction =
                TextAction(SpannableStringBuilder(), Point(event.x, event.y), textPaint)
            showSoftKeyboard()
        }
    }

    private fun otherLocationClicked(event: MotionEvent): Boolean {
        // At the start there's no [currentTextAction] so we didn't click an other location
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
                    drawingContext.view.invalidate()
                    return true
                }
            } else { // text character
                currentTextAction?.addCharacter(event.unicodeChar.toChar())
                drawingContext.view.invalidate()
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
}