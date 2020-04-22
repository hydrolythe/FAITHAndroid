package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail

import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.EXTERNAL_VIDEO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.FILM_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.VIDEO_DETAIL

object DetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
    const val DRAW_DETAIL = 4
    const val VIDEO_DETAIL = 5
    const val EXTERNAL_VIDEO_DETAIL = 6
    const val FILM_DETAIL = 7
}

class DetailThumbnailsAdapter(
    details: List<Detail>,
    private val existingDetailNavigationListener: DetailViewHolder.ExistingDetailNavigationListener
) : RecyclerView.Adapter<DetailViewHolder>() {

    private var _details = details.toMutableList()
    private var hide = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolderFactory.createViewHolder(
                parent,
                viewType,
                existingDetailNavigationListener
        )
    }

    fun hide(hide: Boolean) {
        this.hide = hide
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return _details.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (_details[position]) {
            is AudioDetail -> AUDIO_DETAIL
            is DrawingDetail -> PICTURE_DETAIL
            is TextDetail -> TEXT_DETAIL
            is PhotoDetail -> PICTURE_DETAIL
            is ExternalVideoDetail -> EXTERNAL_VIDEO_DETAIL
            is YoutubeVideoDetail -> VIDEO_DETAIL
            is FilmDetail -> FILM_DETAIL
        }
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(_details[position])
        holder.hide(this.hide)
    }

    fun updateDetailsList(newDetails: List<Detail>) {
        val diffCallback = ThumbnailDiffCallback(_details, newDetails)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _details.clear()
        _details.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
    }
}
