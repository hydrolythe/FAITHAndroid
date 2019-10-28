package com.divyanshu.draw.widget

import android.graphics.Color
import android.graphics.Paint

data class PaintOptions(
    var color: Int = Color.BLACK,
    var strokeWidth: Float = 8f,
    var alpha: Int = 255,
    var isEraserOn: Boolean = false
) {
    constructor(paint: Paint) : this(paint.color, paint.strokeWidth, paint.alpha)
}
