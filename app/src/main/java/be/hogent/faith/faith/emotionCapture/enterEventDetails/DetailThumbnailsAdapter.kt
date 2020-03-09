package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL

object DetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
    const val DRAW_DETAIL = 4
}

class DetailThumbnailsAdapter(
    details: List<Detail>,
    private val existingDetailNavigationListener: DetailViewHolder.ExistingDetailNavigationListener
) : RecyclerView.Adapter<DetailViewHolder>() {

    private var _details = details.toMutableList()
    private var _detailsCopy = details.toMutableList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolderFactory.createViewHolder(
            parent,
            viewType,
            existingDetailNavigationListener
        )
    }

    override fun getItemCount(): Int {
        return _details.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (_details[position]) {
            is AudioDetail -> AUDIO_DETAIL
            is DrawingDetail -> PICTURE_DETAIL
            is TextDetail -> TEXT_DETAIL
            is PhotoDetail -> PICTURE_DETAIL
        }
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(_details[position])
    }

    fun updateDetailsList(newDetails: List<Detail>) {
        val diffCallback = ThumbnailDiffCallback(_details, newDetails)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _detailsCopy.addAll(newDetails)
        _details.clear()
        _details.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterType(type: Int) {
        when (type) {
            AUDIO_DETAIL -> if (_details == _detailsCopy.toMutableList()) _details =
                _detailsCopy.filterIsInstance<AudioDetail>().toMutableList() else _details =
                _detailsCopy
            DRAW_DETAIL -> if (_details == _detailsCopy.toMutableList()) _details =
                _detailsCopy.filterIsInstance<DrawingDetail>().toMutableList() else _details =
                _detailsCopy
            TEXT_DETAIL -> if (_details == _detailsCopy.toMutableList()) _details =
                _detailsCopy.filterIsInstance<TextDetail>().toMutableList() else _details =
                _detailsCopy
            PICTURE_DETAIL -> if (_details == _detailsCopy.toMutableList()) _details =
                _detailsCopy.filterIsInstance<PhotoDetail>().toMutableList() else _details =
                _detailsCopy
        }
        notifyDataSetChanged()
    }
}
