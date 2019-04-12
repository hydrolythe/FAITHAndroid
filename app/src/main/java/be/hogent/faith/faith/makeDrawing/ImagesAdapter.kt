package be.hogent.faith.faith.makeDrawing

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R

class ImagesAdapter(private val context: Context, @IdRes private val imageDrawableIDs: List<Int>) :
    RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val thumbnail = LayoutInflater.from(parent.context).inflate(
            R.layout.item_draw_image,
            parent,
            false
        ) as ImageView
        return ImageViewHolder(thumbnail)
    }

    override fun getItemCount(): Int {
        return imageDrawableIDs.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageDrawableIDs[position])
        // The ID of the Drawable is set as the tag so the [DragOnTouchListener] can use it for the DragShadow.
        holder.imageView.tag = imageDrawableIDs[position]
        holder.imageView.setOnLongClickListener(DragOnTouchListener())
    }
}

class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)