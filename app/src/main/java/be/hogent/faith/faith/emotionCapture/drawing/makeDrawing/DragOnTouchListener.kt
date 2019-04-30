@file:Suppress("DEPRECATION")

package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View

class DragOnTouchListener : View.OnLongClickListener {
    override fun onLongClick(view: View): Boolean {
        val drawableResourceId = view.tag as Int

        // We have to provide the Id of the Drawable as data to the [DragListener]
        //  so it can pass this on to the DrawView.
        val data = ClipData.newPlainText("resource identifier", drawableResourceId.toString())

        // Using a [FullSizeDragShadowBuilder] to show the full-sized Drawable while dragging.
        val drawable = view.context.resources.getDrawable(drawableResourceId)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val shadowBuilder = FullSizeDragShadowBuilder(drawable, view)

        view.startDrag(data, shadowBuilder, view, 0)
        return true
    }

    /**
     * A custom DragShadowBuilder that will show the given [dragShadowDrawable] at full size.
     * The image will drawn with the touch point at (0,0).
     * This means the image will be drawn on the right and below the touch point.
     */
    inner class FullSizeDragShadowBuilder(
        private val dragShadowDrawable: Drawable,
        view: View
    ) : View.DragShadowBuilder(view) {

        override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
            outShadowSize.x = dragShadowDrawable.intrinsicWidth
            outShadowSize.y = dragShadowDrawable.intrinsicHeight
            // Center on top left corner so it matches on DrawView, and so you can clearly see the image while dragging
            outShadowTouchPoint.x = 0
            outShadowTouchPoint.y = 0
        }

        override fun onDrawShadow(canvas: Canvas) {
            dragShadowDrawable.draw(canvas)
        }
    }
}
