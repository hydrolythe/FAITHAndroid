package be.hogent.faith.faith.library.eventList

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.faith.models.Event

class EventListDiffCallback(
    private val oldEvent: List<Event>,
    private val newEvents: List<Event>,
    private val oldShowDelete: Boolean,
    private val newShowDelete: Boolean
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldEvent[oldItemPosition] == newEvents[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldEvent.size
    }

    override fun getNewListSize(): Int {
        return newEvents.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldEvent[oldItemPosition]
        val newItem = newEvents[newItemPosition]

        return oldItem.uuid == newItem.uuid && oldShowDelete == newShowDelete
    }
}