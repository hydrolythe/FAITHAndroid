package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.AudioDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.ExistingDetailNavigationListener
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.ExternalVideoDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.PictureDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.TextDetailViewHolder
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.faith.util.getDefaultThumbnailUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.signature.MediaStoreSignature
import kotlinx.android.synthetic.main.detail_item_rv.view.btn_delete_detailRv
import kotlinx.android.synthetic.main.detail_item_rv.view.detail_img
import kotlinx.android.synthetic.main.detail_item_rv.view.text_detail_title
import org.koin.core.KoinComponent
import org.koin.core.inject

object DetailViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): DetailViewHolder {
        val thumbnailView = LayoutInflater.from(parent.context).inflate(
            R.layout.detail_item_rv,
            parent,
            false
        ) as LinearLayout

        return when (viewType) {
            DetailTypes.AUDIO_DETAIL -> createAudioDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.PICTURE_DETAIL -> createPictureDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.EXTERNAL_VIDEO_DETAIL -> createExternalVideoDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.VIDEO_DETAIL -> createYoutubeVideoDetailViewholder(
                thumbnailView,
                existingDetailNavigationListener
            )
            // TEXT_DETAIL
            else -> createTextDetailViewHolder(
                    thumbnailView,
                    existingDetailNavigationListener
            )
        }
    }

    private fun createAudioDetailViewHolder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): AudioDetailViewHolder {
        thumbnailView.background = null
        return AudioDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createTextDetailViewHolder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): TextDetailViewHolder {
        thumbnailView.background = null
        return TextDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createPictureDetailViewHolder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): PictureDetailViewHolder {
        return PictureDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createExternalVideoDetailViewHolder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): ExternalVideoDetailViewHolder {
        return ExternalVideoDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createYoutubeVideoDetailViewholder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): DetailViewHolder.YoutubeVideoDetailViewHolder {
        return DetailViewHolder.YoutubeVideoDetailViewHolder(
            thumbnailView,
            existingDetailNavigationListener
        )
    }
}

sealed class DetailViewHolder(
    val thumbnailView: LinearLayout,
    private val existingDetailNavigationListener: ExistingDetailNavigationListener
) : RecyclerView.ViewHolder(thumbnailView), KoinComponent {

    // clicklisteners on separate views to make sure there's no overlap
    fun bind(detail: Detail, isDeletable: Boolean) {
        load(detail).into(thumbnailView.detail_img)
        thumbnailView.setTag(R.id.TAG_DETAIL, detail)
        thumbnailView.detail_img.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        thumbnailView.text_detail_title.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        thumbnailView.btn_delete_detailRv.setOnClickListener {
            existingDetailNavigationListener.deleteDetail(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        thumbnailView.text_detail_title.text = detail.title
        setDeletable(isDeletable)
    }

    private fun setDeletable(deletable: Boolean) {
        val deleteBtn = thumbnailView.btn_delete_detailRv
        if (!deletable) {
            deleteBtn.visibility = View.INVISIBLE
            deleteBtn.isClickable = false
        } else {
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.isClickable = true
        }
    }

    abstract fun load(detail: Detail): RequestBuilder<Drawable>

    class TextDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.event_detail_text)
        }
    }

    class PictureDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        val androidTempFileProvider: TempFileProvider by inject()
        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView)
                .load(Base64.decode(detail.thumbnail, Base64.DEFAULT))
/*
            return Glide.with(thumbnailView)
                    .load(detail.file)
                    // Signature is required to force Glide to reload overwritten pictures
                    .signature(MediaStoreSignature("", detail.file.lastModified(), 0))
                    */

        }
    }

    class AudioDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.event_detail_audio)
        }
    }

    class ExternalVideoDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.event_detail_camera) // Vervangen door?
        }
    }

    class YoutubeVideoDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            require(detail is YoutubeVideoDetail)
            return Glide.with(thumbnailView).load(getDefaultThumbnailUrl(detail.videoId))
        }
    }

    interface ExistingDetailNavigationListener {
        fun openDetailScreenFor(detail: Detail)
        fun deleteDetail(detail: Detail)
    }
}