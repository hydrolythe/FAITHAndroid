package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.divyanshu.draw.widget.DrawView
import kotlin.math.roundToInt

class DragListener(private val drawView: DrawView) : OnDragListener {

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                val drawableResourceId =
                    Integer.parseInt((event.clipData.getItemAt(0).text).toString())
                drawView.addDrawable(drawableResourceId, event.x.roundToInt(), event.y.roundToInt())
            }
        }
        return true // Keep listening to drag events
    }
}