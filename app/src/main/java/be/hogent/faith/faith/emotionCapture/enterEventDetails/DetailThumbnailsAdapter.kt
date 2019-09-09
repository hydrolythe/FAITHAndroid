package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import com.bumptech.glide.Glide

class DetailThumbnailsAdapter(private val context: Context, details: List<Detail>) :
    RecyclerView.Adapter<ThumbnailViewHolder>() {

    private val _details = details.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val thumbnail = LayoutInflater.from(parent.context).inflate(
            R.layout.item_event_detail_thumbnail,
            parent,
            false
        ) as ImageView
        return ThumbnailViewHolder(thumbnail)
    }

    override fun getItemCount(): Int {
        return _details.size
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val thumbnail: Any = when (_details[position]) {
            is AudioDetail -> R.drawable.event_detail_audio
            is PictureDetail -> _details[position].file
            is TextDetail -> R.drawable.event_detail_text
            else -> R.drawable.avatar_placeholder
        }
        Glide.with(context)
            .load(thumbnail)
            .into(holder.imageView)
    }

    fun updateDetailsList(newDetails: List<Detail>) {
        val diffCallback = ThumbnailDiffCallback(_details, newDetails)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        _details.clear()
        _details.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
    }
}

class ThumbnailViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)