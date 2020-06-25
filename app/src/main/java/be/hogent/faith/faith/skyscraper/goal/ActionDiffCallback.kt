package be.hogent.faith.faith.skyscraper.goal

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.domain.models.goals.Action

class ActionDiffCallback : DiffUtil.ItemCallback<Action>() {

    override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
        return oldItem.description == newItem.description && oldItem.currentStatus == newItem.currentStatus
    }

    override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
        return oldItem == newItem
    }
}