package be.hogent.faith.faith.skyscraper.goal

import android.view.DragEvent
import android.view.View
import android.widget.ImageView

class AvatarOnDragListener(private val avatarOnTouchListener: AvatarOnTouchListener) :
    View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent): Boolean {

        val startLocation = event.localState as ImageView
        val targetLocation = v as ImageView
        if (event.action == DragEvent.ACTION_DROP) {
            if(startLocation.drawable != targetLocation.drawable ) {
                targetLocation.setOnTouchListener(avatarOnTouchListener)
                targetLocation.setImageDrawable(startLocation.drawable)
                startLocation.setImageDrawable(null)
                startLocation.setOnTouchListener(null)
            }
        }
        return true
    }
}
