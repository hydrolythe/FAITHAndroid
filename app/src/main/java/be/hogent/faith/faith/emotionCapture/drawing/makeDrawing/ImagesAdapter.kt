package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R

class ImagesAdapter(@IdRes imageDrawableIDs: List<Int>) :
    RecyclerView.Adapter<ImageViewHolder>() {

    private val imageResources = mutableListOf<Int>()

    init {
        imageResources.addAll(imageDrawableIDs)
    }

    fun setNewImages(newImages: List<Int>) {
        imageResources.clear()
        imageResources.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val thumbnail = LayoutInflater.from(parent.context).inflate(
            R.layout.item_draw_image,
            parent,
            false
        ) as ImageView
        return ImageViewHolder(thumbnail)
    }

    override fun getItemCount(): Int {
        return imageResources.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageResources[position])
        // The ID of the Drawable is set as the tag so the [DragOnTouchListener] can use it for the DragShadow.
        holder.imageView.tag = imageResources[position]
        holder.imageView.setOnLongClickListener(DragOnTouchListener())
    }
}

class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)