package com.divyanshu.draw.widget.tools.drawing

import android.graphics.Color
import android.graphics.Paint
import com.divyanshu.draw.widget.tools.DrawingContext

class EraserTool(
    drawingContext: DrawingContext,
    paint: Paint
) : DrawingTool(drawingContext, paint) {

    override fun startNewPath() {
        currentPath = PathAction(Paint(paint).apply {
            color = Color.WHITE
        })
    }
}