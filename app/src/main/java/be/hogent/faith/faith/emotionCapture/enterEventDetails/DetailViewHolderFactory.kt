package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

object DetailViewHolderFactory {
    fun createViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val thumbnailView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_event_detail_thumbnail,
            parent,
            false
        ) as ImageView

        return when (viewType) {
            DetailTypes.AUDIO_DETAIL -> createAudioDetailViewHolder(thumbnailView)
            DetailTypes.PICTURE_DETAIL -> createPictureDetailViewHolder(thumbnailView)
            DetailTypes.TEXT_DETAIL -> createTextDetailViewHolder(thumbnailView)
            else -> throw IllegalArgumentException("unknown viewType given")
        }
    }

    private fun createAudioDetailViewHolder(thumbnailView: ImageView):
            DetailViewHolder.AudioDetailViewHolder {
        return DetailViewHolder.AudioDetailViewHolder(thumbnailView)
    }

    private fun createTextDetailViewHolder(thumbnailView: ImageView):
            DetailViewHolder.TextDetailViewHolder {
        return DetailViewHolder.TextDetailViewHolder(thumbnailView)
    }

    private fun createPictureDetailViewHolder(thumbnailView: ImageView):
            DetailViewHolder.PictureDetailViewHolder {
        return DetailViewHolder.PictureDetailViewHolder(thumbnailView)
    }
}

sealed class DetailViewHolder(val imageView: ImageView) :
    RecyclerView.ViewHolder(imageView) {

    fun bind(detail: Detail) {
        load(detail).into(imageView)
    }

    abstract fun load(detail: Detail): RequestBuilder<Drawable>

    class TextDetailViewHolder(imageView: ImageView) :
        DetailViewHolder(imageView) {
        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(R.drawable.event_detail_text)
        }
    }

    class PictureDetailViewHolder(imageView: ImageView) :
        DetailViewHolder(imageView) {
        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(detail.file)
        }
    }

    class AudioDetailViewHolder(imageView: ImageView) :
        DetailViewHolder(imageView) {
        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(R.drawable.event_detail_audio)
        }
    }
}
