package be.hogent.faith.faith.enterEventDetails

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.domain.models.detail.Detail

class ThumbnailDiffCallback(
    private val oldDetails: List<Detail>,
    private val newDetails: List<Detail>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDetails[oldItemPosition] == newDetails[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldDetails.size
    }

    override fun getNewListSize(): Int {
        return newDetails.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldDetails[oldItemPosition]
        val newItem = newDetails[newItemPosition]

        return oldItem.uuid == newItem.uuid
    }
}