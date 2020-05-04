package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.recyclerview.widget.DiffUtil
import be.hogent.faith.domain.models.detail.Detail

class ThumbnailDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem == newItem
    }
}