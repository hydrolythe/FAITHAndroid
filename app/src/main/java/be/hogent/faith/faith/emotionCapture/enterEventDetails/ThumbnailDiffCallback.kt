package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.skyscraper.startscreen.Skyscraper

class ThumbnailDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem == newItem
    }
}
class SkyscraperThumbnailDiffCallback : DiffUtil.ItemCallback<Skyscraper>() {
    override fun areItemsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
        return oldItem == newItem
    }
}