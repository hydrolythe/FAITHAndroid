package com.divyanshu.draw.widget

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log

// TODO: change to a subclass of drawable? Might not be possible because there are already subclasses of Drawable
class MyDrawable(private val drawable: Drawable) : DrawingAction {
    override fun drawOn(canvas: Canvas) {
        Log.d("DrawView", "Drawing drawable $drawable on ${drawable.bounds.left}, ${drawable.bounds.top}")
        drawable.draw(canvas)
    }
}