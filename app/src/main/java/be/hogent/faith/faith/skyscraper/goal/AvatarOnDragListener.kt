package be.hogent.faith.faith.skyscraper.goal

import android.view.DragEvent
import android.view.View
import android.widget.ImageView

class AvatarOnDragListener(
    private val avatarDroppedListener: AvatarDroppedListener,
    private val avatar: ImageView
) : View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent): Boolean {

        val startLocation = avatar
        val targetLocation = v as? ImageView
        if (event.action == DragEvent.ACTION_DROP && targetLocation != null) {
            // if the tops are different
            if (startLocation.y != targetLocation.y) {
                // tag contains the AvatarPosition
                val position = targetLocation.tag.toString().toInt()
                val extra = if (position % 2 != 0) targetLocation.height / 2 else 0
                // top
                startLocation.y =
                    targetLocation.y + targetLocation.height - startLocation.height + extra
                // left
                startLocation.x =
                    targetLocation.x + (targetLocation.width / 2 - startLocation.width / 2).toFloat()
                avatarDroppedListener.onAvatarDropped(targetLocation.tag.toString().toInt())
            }
        }
        return true
    }

    interface AvatarDroppedListener {
        fun onAvatarDropped(floor: Int)
    }
}
