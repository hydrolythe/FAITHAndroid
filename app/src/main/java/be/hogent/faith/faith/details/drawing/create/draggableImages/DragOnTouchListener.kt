@file:Suppress("DEPRECATION")

package be.hogent.faith.faith.details.drawing.create.draggableImages

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toBitmap

const val SCALEFACTORHEIGHT = 8

class DragOnTouchListener(private val dropView: View) : View.OnClickListener {
    override fun onClick(view: View) {
        val drawableResourceId = view.tag as Int

        // We have to provide the Id of the Drawable as data to the [DragListener]
        //  so it can pass this on to the DrawView.
        val data = ClipData.newPlainText("resource identifier", drawableResourceId.toString())

        // Using a [FullSizeDragShadowBuilder] to show the full-sized Drawable while dragging.
        var drawable = view.context.resources.getDrawable(drawableResourceId)
        // but first rescale the drawable to 1/8th of the height of the drawView
        val scalefactor = drawable.intrinsicHeight.toFloat() / (dropView.height / SCALEFACTORHEIGHT).toFloat()
        val newWidth = (drawable.intrinsicWidth / scalefactor).toInt()
        val newHeight = dropView.height / SCALEFACTORHEIGHT

        val resizedBitmap = Bitmap.createScaledBitmap(
            drawable.toBitmap(), newWidth, newHeight, false
        )
        drawable = BitmapDrawable(view.context.getResources(), resizedBitmap)

        drawable.setBounds(0, 0, newWidth,
            newHeight)
        val shadowBuilder = FullSizeDragShadowBuilder(drawable, view)

        view.startDrag(data, shadowBuilder, view, 0)
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
            outShadowSize.x = dragShadowDrawable.bounds.width()
            outShadowSize.y = dragShadowDrawable.bounds.height()
            // Center on top left corner so it matches on DrawView, and so you can clearly see the image while dragging
            outShadowTouchPoint.x = 0
            outShadowTouchPoint.y = 0
        }

        override fun onDrawShadow(canvas: Canvas) {
            dragShadowDrawable.draw(canvas)
        }
    }
}
