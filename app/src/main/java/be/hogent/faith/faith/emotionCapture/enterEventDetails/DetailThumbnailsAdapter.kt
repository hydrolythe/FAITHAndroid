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
        return DetailViewHolderFactory.createViewHolder(parent, viewType, existingDetailNavigationListener)
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
    fun filterSearchBar(text:String){
        //TODO update for detail.title
        _details.clear()
        if(text.isEmpty()){
            _details.addAll(_detailsCopy)
        }else{
            for(detail in _detailsCopy){
                if(detail.uuid.toString().toLowerCase().contains(text.toLowerCase())){
                    _details.add(detail)
                }
            }
        }
        notifyDataSetChanged()

    }
    fun filterType(type: Int){
        when(type){
             AUDIO_DETAIL -> _details = _detailsCopy.filterIsInstance<AudioDetail>().toMutableList()
             DRAW_DETAIL ->_details = _detailsCopy.filterIsInstance<DrawingDetail>().toMutableList()
             TEXT_DETAIL -> _details = _detailsCopy.filterIsInstance<TextDetail>().toMutableList()
             PICTURE_DETAIL -> _details = _detailsCopy.filterIsInstance<PhotoDetail>().toMutableList()
        }
        notifyDataSetChanged()
    }

}
