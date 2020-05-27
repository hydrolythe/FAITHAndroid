package be.hogent.faith.faith.skyscraper

import android.view.DragEvent
import android.view.View
import android.widget.ImageView

class DragDropOnDragListener(private val dragDropOnTouchListener: DragDropOnTouchListener) :
    View.OnDragListener {

    // private var invalidDrop = false
    override fun onDrag(v: View?, event: DragEvent): Boolean {

        val startLocation = event.localState as ImageView
        val targetLocation = v as ImageView
        if (event.action == DragEvent.ACTION_DROP) {
            targetLocation.setOnTouchListener(dragDropOnTouchListener)
            targetLocation.setImageDrawable(startLocation.drawable)
            startLocation.setImageDrawable(null)
            startLocation.setOnTouchListener(null)
        }
        return true
    }
}
