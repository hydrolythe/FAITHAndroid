package be.hogent.faith.faith.makeDrawing

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import com.bumptech.glide.Glide

class ImagesAdapter(private val context: Context, private val imageDrawableIDs: List<Int>) :
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
        Glide.with(context)
            .load(imageDrawableIDs[position])
            .into(holder.imageView)
        holder.imageView.setOnTouchListener(DragOnTouchListener())
    }
}

class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)