package com.divyanshu.draw.widget.tools

import android.view.View
import com.divyanshu.draw.widget.DrawingAction

interface DrawingContext {
    fun clearUndoneActions()
    fun addDrawingAction(action: DrawingAction)

    fun openSoftKeyboard()

    val view: View
}