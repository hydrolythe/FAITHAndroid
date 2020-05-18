package com.divyanshu.draw.widget.tools.drawing

import android.graphics.Color
import android.graphics.Paint
import com.divyanshu.draw.widget.tools.DrawingContext

class EraserTool(
    drawingContext: DrawingContext,
    paint: Paint,
    private val colorAlpha: Int
) : DrawingTool(drawingContext, paint) {

    override fun startNewPath() {
        currentPath = PathAction(Paint(paint).apply {
            color = Color.WHITE
            alpha = colorAlpha
        })
    }
}