@file:Suppress("DEPRECATION")

package be.hogent.faith.faith.makeDrawing

import android.content.ClipData
import android.view.View

const val DRAWABLE_RES = "drawable resource identifier"

class DragOnTouchListener() : View.OnLongClickListener {
    override fun onLongClick(view: View): Boolean {
        val data = ClipData.newPlainText(DRAWABLE_RES, view.tag as String)
        val shadowBuilder = View.DragShadowBuilder(
            view
        )
        view.startDrag(data, shadowBuilder, view, 0)
        return true
    }
}
