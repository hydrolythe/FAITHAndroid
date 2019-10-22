package com.divyanshu.draw.widget

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.divyanshu.draw.widget.tools.CanvasAction

// TODO: change to a subclass of drawable? Might not be possible because there are already subclasses of Drawable
class DrawableAction(private val drawable: Drawable) : CanvasAction {
    override fun drawOn(canvas: Canvas) {
        drawable.draw(canvas)
    }
}