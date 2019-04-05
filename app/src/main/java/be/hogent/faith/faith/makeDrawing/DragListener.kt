package be.hogent.faith.faith.makeDrawing

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import be.hogent.faith.util.TAG

class DragListener : OnDragListener {

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d(TAG, "Started dragging")
            }
            DragEvent.ACTION_DROP -> {
                Log.d(TAG, "Stopped dragging, dropped at x: ${event.x}, y: ${event.y}")
                Log.d(TAG, "event: ${event.x}, y: ${event.y}")
            }
        }
        return true // Keep listening to drag events
    }
}