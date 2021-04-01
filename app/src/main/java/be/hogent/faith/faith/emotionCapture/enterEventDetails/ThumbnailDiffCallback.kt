package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.goals.Goal

class ThumbnailDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem == newItem
    }
}
class SkyscraperThumbnailDiffCallback : DiffUtil.ItemCallback<Goal>() {
    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem == newItem
    }
}