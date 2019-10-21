package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.divyanshu.draw.widget.DrawView
import timber.log.Timber
import kotlin.math.roundToInt

class DragListener(private val drawView: DrawView) : OnDragListener {

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Timber.d("Started dragging")
            }
            DragEvent.ACTION_DROP -> {
                Timber.d("Stopped dragging, dropped at x: ${event.x}, y: ${event.y}")
                Timber.d("event: ${event.x}, y: ${event.y}")

                drawView.addDrawable(
                    Integer.parseInt((event.clipData.getItemAt(0).text).toString()),
                    event.x.roundToInt(),
                    event.y.roundToInt()
                )
            }
        }
        return true // Keep listening to drag events
    }
}