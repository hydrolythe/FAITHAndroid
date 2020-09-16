package be.hogent.faith.faith.details.drawing.create.draggableImages

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.core.graphics.drawable.toBitmap
import be.hogent.faith.R
import com.divyanshu.draw.widget.DrawView
import com.divyanshu.draw.widget.DrawableAction
import timber.log.Timber
import kotlin.math.roundToInt

const val SCALEFACTOR_ITEMS = 8
const val SCALEFACTOR_METHAPORS = 3

class DragListener(private val drawView: DrawView) : OnDragListener {

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Timber.d("Started dragging")
            }
            DragEvent.ACTION_DROP -> {
                val drawableResourceID =
                    Integer.parseInt((event.clipData.getItemAt(0).text).toString())
                val resources = drawView.context.resources
                var drawable = resources.getDrawable(drawableResourceID)
                var array = resources.getStringArray(R.array.templates_decor)
                val isBackgroundImage =
                    !array.filter { it.endsWith("${resources.getResourceEntryName(drawableResourceID)}.png") }
                        .isEmpty()
                array = resources.getStringArray(R.array.templates_metaphors)
                val isMetaphoreImage =
                    !array.filter { it.endsWith("${resources.getResourceEntryName(drawableResourceID)}.png") }
                        .isEmpty()
                if (!isBackgroundImage) {
                    val height =
                        if (isMetaphoreImage) drawView.height / SCALEFACTOR_METHAPORS else drawView.height / SCALEFACTOR_ITEMS
                    val scalefactor =
                        drawable.intrinsicHeight.toFloat() / height.toFloat()
                    val newWidth = (drawable.intrinsicWidth / scalefactor).toInt()
                    val newHeight = (drawable.intrinsicHeight / scalefactor).toInt()
                    Timber.i("bitmap $newWidth and $newHeight")

                    val resizedBitmap = Bitmap.createScaledBitmap(
                        drawable.toBitmap(), newWidth, newHeight, false
                    )
                    drawable = BitmapDrawable(drawView.context.getResources(), resizedBitmap)
                    drawable.bounds = Rect(
                        event.x.roundToInt(),
                        event.y.roundToInt(),
                        event.x.roundToInt() + drawable.intrinsicWidth,
                        event.y.roundToInt() + drawable.intrinsicHeight
                    )
                    drawView.addDrawingAction(DrawableAction(drawable))
                    drawView.invalidate()
                } else {
                    val resizedBitmap = Bitmap.createScaledBitmap(
                        drawable.toBitmap(), drawView.width, drawView.height, false
                    )
                    drawable = BitmapDrawable(drawView.context.getResources(), resizedBitmap)
                    drawable.bounds = Rect(
                        0,
                        0,
                        drawView.width,
                        drawView.height
                    )
                    drawView.addDrawingAction(DrawableAction(drawable))
                    drawView.invalidate()
                }
            }
        }
        return true // Keep listening to drag events
    }
}