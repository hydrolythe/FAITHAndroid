package com.divyanshu.draw.widget

import android.graphics.Canvas

interface DrawingAction {
    fun drawOn(canvas: Canvas)
}