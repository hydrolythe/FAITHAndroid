package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.AudioDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.ExistingDetailNavigationListener
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.PictureDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.TextDetailViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

object DetailViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): DetailViewHolder {
        val thumbnailView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_event_detail_thumbnail,
            parent,
            false
        ) as ImageView

        return when (viewType) {
            DetailTypes.AUDIO_DETAIL -> createAudioDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.PICTURE_DETAIL -> createPictureDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.TEXT_DETAIL -> createTextDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            else -> throw IllegalArgumentException("unknown viewType given")
        }
    }

    private fun createAudioDetailViewHolder(
        thumbnailView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): AudioDetailViewHolder {
        return AudioDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createTextDetailViewHolder(
        thumbnailView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): TextDetailViewHolder {
        return TextDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createPictureDetailViewHolder(
        thumbnailView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): PictureDetailViewHolder {
        return PictureDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }
}

sealed class DetailViewHolder(
    val imageView: ImageView,
    private val existingDetailNavigationListener: ExistingDetailNavigationListener
) : RecyclerView.ViewHolder(imageView) {

    fun bind(detail: Detail) {
        load(detail).into(imageView)
        imageView.setTag(R.id.TAG_DETAIL, detail)
        imageView.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(imageView.getTag(R.id.TAG_DETAIL) as Detail)
        }
    }

    abstract fun load(detail: Detail): RequestBuilder<Drawable>

    class TextDetailViewHolder(
        imageView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(R.drawable.event_detail_text)
        }
    }

    class PictureDetailViewHolder(
        imageView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(detail.file)
        }
    }

    class AudioDetailViewHolder(
        imageView: ImageView,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(imageView).load(R.drawable.event_detail_audio)
        }
    }

    interface ExistingDetailNavigationListener {
        fun openDetailScreenFor(detail: Detail)
    }
}
