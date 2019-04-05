@file:Suppress("DEPRECATION")

package be.hogent.faith.faith.makeDrawing

import android.content.ClipData
import android.view.MotionEvent
import android.view.View

class DragOnTouchListener : View.OnTouchListener {

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(
                    view
                )
                view.startDrag(data, shadowBuilder, view, 0)
                return true
            }
            else ->
                return false
        }
    }
}
