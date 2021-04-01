package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.detail.VideoDetail
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.FILM_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.VIDEO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.YOUTUBE_DETAIL
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import be.hogent.faith.faith.models.detail.FilmDetail

object DetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
    const val DRAW_DETAIL = 4
    const val YOUTUBE_DETAIL = 5
    const val VIDEO_DETAIL = 6
    const val FILM_DETAIL = 7
}

class DetailThumbnailsAdapter(
    private val existingDetailNavigationListener: DetailViewHolder.ExistingDetailNavigationListener
) : ListAdapter<Detail, DetailViewHolder>(ThumbnailDiffCallback()) {

    /**
     * Indicates whether the items shown in the RecyclerView should show a "delete" icon, and respond
     * to clicks on this icon.
     */
    private var itemsAreDeletable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolderFactory.createViewHolder(
            parent,
            viewType,
            existingDetailNavigationListener
        )
    }

    fun setItemsAsDeletable(deletable: Boolean) {
        this.itemsAreDeletable = deletable
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AudioDetail -> AUDIO_DETAIL
            is DrawingDetail -> DRAW_DETAIL
            is TextDetail -> TEXT_DETAIL
            is PhotoDetail -> PICTURE_DETAIL
            is VideoDetail -> VIDEO_DETAIL
            is YoutubeVideoDetail -> YOUTUBE_DETAIL
            is FilmDetail -> FILM_DETAIL
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position), itemsAreDeletable)
    }
}
