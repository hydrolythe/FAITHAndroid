package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.AudioDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.ExistingDetailNavigationListener
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.PictureDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.TextDetailViewHolder
import be.hogent.faith.faith.util.TempFileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.signature.MediaStoreSignature
import kotlinx.android.synthetic.main.detail_item_rv.view.detail_img
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
        ) as ConstraintLayout

        return when (viewType) {
            DetailTypes.AUDIO_DETAIL -> createAudioDetailViewHolder(
                thumbnailView,
                existingDetailNavigationListener
            )
            DetailTypes.PICTURE_DETAIL -> createPictureDetailViewHolder(
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
        thumbnailView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): AudioDetailViewHolder {
        thumbnailView.background = null
        return AudioDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createTextDetailViewHolder(
        thumbnailView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): TextDetailViewHolder {
        thumbnailView.background = null
        return TextDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }

    private fun createPictureDetailViewHolder(
        thumbnailView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): PictureDetailViewHolder {
        return PictureDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }
}

sealed class DetailViewHolder(
    val thumbnailView: ConstraintLayout,
    private val existingDetailNavigationListener: ExistingDetailNavigationListener
) : RecyclerView.ViewHolder(thumbnailView), KoinComponent {

    fun bind(detail: Detail) {
        load(detail).into(thumbnailView.detail_img)
        thumbnailView.setTag(R.id.TAG_DETAIL, detail)
        thumbnailView.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        val detailTitle = thumbnailView.getViewById(R.id.text_detail_title) as TextView
        detailTitle.text = detail.fileName
    }

    fun hide(hide : Boolean){
        val deleteBtn = thumbnailView.getViewById(R.id.btn_delete_detailRv)
        if(hide){
            deleteBtn.visibility = View.INVISIBLE
           deleteBtn.isClickable = false
        }
        else{
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.isClickable = true
        }
    }

    abstract fun load(detail: Detail): RequestBuilder<Drawable>

    class TextDetailViewHolder(
        imageView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.event_detail_text)
        }
    }

    class PictureDetailViewHolder(
        imageView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        val androidTempFileProvider: TempFileProvider by inject()
        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView)
                .load(androidTempFileProvider.getFile(detail))
                // Signature is required to force Glide to reload overwritten pictures
                .signature(MediaStoreSignature("", detail.file.lastModified(), 0))
        }
    }

    class AudioDetailViewHolder(
        imageView: ConstraintLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.event_detail_audio)
        }
    }

    interface ExistingDetailNavigationListener {
        fun openDetailScreenFor(detail: Detail)
    }
}