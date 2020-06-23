package be.hogent.faith.faith.skyscraper.goal

import android.view.DragEvent
import android.view.View
import android.widget.ImageView

class AvatarOnDragListener(
    private val avatarDropped: AvatarDropped
) :
    View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent): Boolean {

        val startLocation = event.localState as? ImageView
        val targetLocation = v as? ImageView
        if (startLocation != null && targetLocation != null && event.action == DragEvent.ACTION_DROP) {
            // if the tops are different
            if (startLocation.y != targetLocation.y) {
                // targetLocation.setOnTouchListener(avatarOnTouchListener)
                val position = targetLocation.tag.toString().toInt()
                val extra = if (position % 2 != 0) targetLocation.height / 2 else 0
                // top
                startLocation.y =
                    targetLocation.y + targetLocation.height - startLocation.height + extra
                // left
                startLocation.x =
                    targetLocation.x + (targetLocation.width / 2 - startLocation.width / 2).toFloat()
                avatarDropped.onAvatarDropped(targetLocation.tag.toString().toInt())
            }
        }
        return true
    }

    interface AvatarDropped {
        fun onAvatarDropped(floor: Int)
    }
}
