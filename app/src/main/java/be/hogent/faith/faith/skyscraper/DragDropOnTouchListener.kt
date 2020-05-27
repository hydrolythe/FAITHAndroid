package be.hogent.faith.faith.skyscraper

import android.content.ClipData
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder

class DragDropOnTouchListener : View.OnTouchListener {
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        // Create clip data.
        val clipData = ClipData.newPlainText("", "Avatar")

        // Create drag shadow builder object.
        val dragShadowBuilder = DragShadowBuilder(view)

        /* Invoke view object's startDrag method to start the drag action.
           clipData : to be dragged data.
           dragShadowBuilder : the shadow of the dragged view.
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view!!.startDragAndDrop(clipData, dragShadowBuilder, view, 0)
        } else {
            view!!.startDrag(clipData, dragShadowBuilder, view, 0)
        }

        return true
    }
}
