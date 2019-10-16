package com.divyanshu.draw.widget.tools.text

import android.graphics.Canvas
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import com.divyanshu.draw.widget.Point
import com.divyanshu.draw.widget.tools.CanvasAction

class TextAction(
    private var writtenText: SpannableStringBuilder,
    val position: Point,
    private val textPaint: TextPaint
) : CanvasAction {

    override fun drawOn(canvas: Canvas) {
        val width = textPaint.measureText(writtenText.toString()).toInt()
        val textLayout = StaticLayout(
            writtenText,
            textPaint,
            width,
            Layout.Alignment.ALIGN_NORMAL,
            1.0f,
            0f,
            false
        )
        // Save original canvas state (see https://html5.litten.com/understanding-save-and-restore-for-the-canvas-context/)
        canvas.save()
        // Do transform
        canvas.translate(position.x, position.y)
        textLayout.draw(canvas)
        // Back to original canvas state
        canvas.restore()
    }

    fun removeLastCharacter() {
        writtenText = writtenText.delete(writtenText.length - 1, writtenText.length)
    }

    fun addCharacter(char: Char) {
        writtenText.append(char)
    }
}
