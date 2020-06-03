package be.hogent.faith.faith.skyscraper.history

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.EXTERNAL_VIDEO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.FILM_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.VIDEO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperThumbnailDiffCallback
import be.hogent.faith.faith.skyscraper.startscreen.Skyscraper
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors


class HistoryAdapter(
    private val skyscraperHistoryNavigationListener: SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener
) : ListAdapter<Skyscraper, SkyscraperThumbnailViewHolder>(SkyscraperThumbnailDiffCallback()) {

    /**
     * Indicates whether the items shown in the RecyclerView should show a "delete" icon, and respond
     * to clicks on this icon.
     */
    private var itemsAreDeletable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkyscraperThumbnailViewHolder {
        return SkyscraperThumbnailViewHolderFactory.createViewHolder(
            parent,
            viewType,
            skyscraperHistoryNavigationListener
        )
    }

    fun setItemsAsDeletable(deletable: Boolean) {
        this.itemsAreDeletable = deletable
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).color.value
        }

    override fun onBindViewHolder(holder: SkyscraperThumbnailViewHolder, position: Int) {
        holder.bind(getItem(position), itemsAreDeletable)
    }
}


