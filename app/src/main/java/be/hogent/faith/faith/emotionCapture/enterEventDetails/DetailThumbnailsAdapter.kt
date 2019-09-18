package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL

object DetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
}

class DetailThumbnailsAdapter(details: List<Detail>) :
    RecyclerView.Adapter<DetailViewHolder>() {

    private val _details = details.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolderFactory.createViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return _details.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (_details[position]) {
            is AudioDetail -> AUDIO_DETAIL
            is PictureDetail -> PICTURE_DETAIL
            is TextDetail -> TEXT_DETAIL
            else -> throw IllegalArgumentException("Unknown DetailType found in DetailAdapter")
        }
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(_details[position])
    }

    fun updateDetailsList(newDetails: List<Detail>) {
        val diffCallback = ThumbnailDiffCallback(_details, newDetails)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        _details.clear()
        _details.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
    }
}
