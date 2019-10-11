package com.divyanshu.draw.widget.tools

import android.view.View

interface DrawingContext {
    fun clearUndoneActions()
    fun addDrawingAction(action: CanvasAction)

    val view: View
}