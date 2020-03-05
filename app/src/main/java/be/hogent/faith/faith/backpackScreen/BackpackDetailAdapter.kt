package be.hogent.faith.faith.backpackScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.DetailItemBinding
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.backpackScreen.BackpackDetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.backpackScreen.BackpackDetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.backpackScreen.BackpackDetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.backpackScreen.BackpackDetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.ThumbnailDiffCallback
import be.hogent.faith.faith.util.AndroidTempFileProvider
import be.hogent.faith.faith.util.TempFileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import org.koin.core.KoinComponent
import org.koin.core.bind
import org.koin.core.inject

object BackpackDetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
    const val DRAW_DETAIL = 4
}

class BackpackDetailAdapter(details: List<Detail>, val clickListener : DetailListener): ListAdapter<Detail, BackpackDetailAdapter.BackpackDetailViewHolder>(BackpackDiffCallback()) {

    private var _details = details.toMutableList()

    fun updateDetailsList(newDetails: List<Detail>) {
        val diffCallback = ThumbnailDiffCallback(_details, newDetails)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
      //  _detailsCopy.addAll(newDetails)
        _details.clear()
        _details.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
        this.submitList(newDetails)
    }

    override fun getItemViewType(position: Int): Int {
        return when (_details[position]) {
            is AudioDetail -> AUDIO_DETAIL
            is DrawingDetail -> DRAW_DETAIL
            is TextDetail -> TEXT_DETAIL
            is PhotoDetail -> PICTURE_DETAIL
        }
    }

    override fun onBindViewHolder(holder: BackpackDetailViewHolder, position : Int){

        holder.binding.backpackDetail.setOnClickListener {
            clickListener.viewDetail(getItem(position)!!)
        }

        holder.binding.backpackDetailDelete.setOnClickListener {
            clickListener.deleteDetail(getItem(position)!!)
        }

        holder.fillViewItems(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackpackDetailViewHolder{
        return BackpackDetailViewHolder.from(parent, viewType)
    }

    class BackpackDetailViewHolder private constructor(val binding: DetailItemBinding, private val viewType: Int): RecyclerView.ViewHolder(binding.root),
        KoinComponent {

        private val androidTempFileProvider: TempFileProvider by inject()

        fun fillViewItems(
            item: Detail,
            clickListener: DetailListener
        ) {
            binding.detail = item
            binding.clickListener = clickListener

            when (viewType) {
                DetailTypes.AUDIO_DETAIL -> (
                        Glide.with(binding.backpackDetail).load(R.drawable.event_detail_audio).into(binding.backpackDetail)
                        )

                DetailTypes.PICTURE_DETAIL -> (
                        Glide.with(binding.backpackDetail)
                            .load(androidTempFileProvider.getFile(binding.detail!! as PhotoDetail))
                            // Signature is required to force Glide to reload overwritten pictures
                            .signature(MediaStoreSignature("", binding.detail!!.file.lastModified(), 0))
                            .into(binding.backpackDetail)
                        )
                DetailTypes.DRAW_DETAIL -> (
                        Glide.with(binding.backpackDetail)
                            .load(androidTempFileProvider.getFile(binding.detail!! as DrawingDetail))
                            // Signature is required to force Glide to reload overwritten pictures
                            .signature(MediaStoreSignature("", binding.detail!!.file.lastModified(), 0))
                            .into(binding.backpackDetail)
                        )
                // TEXT_DETAIL
                else -> (Glide.with(binding.backpackDetail).load(R.drawable.event_detail_text).into(binding.backpackDetail)
                        )
            }

            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup, viewType: Int): BackpackDetailViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DetailItemBinding.inflate(layoutInflater, parent, false)

                return BackpackDetailViewHolder(binding, viewType)
            }
        }
    }
}

class BackpackDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.file == newItem.file
    }

    override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.uuid == newItem.uuid
    }
}

/**
 * Creates onclicklisteners for all the attributes of a beat
 * */
interface DetailListener {
    fun deleteDetail(detail : Detail)
    fun viewDetail (detail : Detail)
}