package be.hogent.faith.faith.skyscraper.goal

import androidx.recyclerview.widget.DiffUtil

class ActionListDiffCallback(
    private val oldActions: List<Action>,
    private val newActions: List<Action>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldActions[oldItemPosition] == newActions[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldActions.size
    }

    override fun getNewListSize(): Int {
        return newActions.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldActions[oldItemPosition]
        val newItem = newActions[newItemPosition]

        return oldItem.description == newItem.description
    }
}