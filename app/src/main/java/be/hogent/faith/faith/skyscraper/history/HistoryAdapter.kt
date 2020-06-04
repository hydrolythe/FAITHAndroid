package be.hogent.faith.faith.skyscraper.history

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperThumbnailDiffCallback
import be.hogent.faith.faith.skyscraper.startscreen.Goal


class HistoryAdapter(
    private val skyscraperHistoryNavigationListener: SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener
) : ListAdapter<Goal, SkyscraperThumbnailViewHolder>(SkyscraperThumbnailDiffCallback()) {

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


