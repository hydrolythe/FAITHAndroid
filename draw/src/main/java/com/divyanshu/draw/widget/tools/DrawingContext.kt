package com.divyanshu.draw.widget.tools

import com.divyanshu.draw.widget.DrawingAction

interface DrawingContext {
    fun clearUndoneActions()
    fun addDrawingAction(action: DrawingAction)
}