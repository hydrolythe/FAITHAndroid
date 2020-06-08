package be.hogent.faith.faith.cinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SelectedDetailItemRvBinding
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.VIDEO_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.FILM_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailTypes.YOUTUBE_DETAIL
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.faith.util.getDefaultThumbnailUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import org.koin.core.KoinComponent
import org.koin.core.inject

class SelectedDetailsAdapter(private val clickListener: SelectedDetailsClickListener) :
    ListAdapter<Detail, SelectedDetailsAdapter.SelectedDetailsViewHolder>(SelectedDetailsDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)!!) {
            is AudioDetail -> AUDIO_DETAIL
            is DrawingDetail -> DRAW_DETAIL
            is TextDetail -> TEXT_DETAIL
            is PhotoDetail -> PICTURE_DETAIL
            is YoutubeVideoDetail -> YOUTUBE_DETAIL
            is VideoDetail -> VIDEO_DETAIL
            is FilmDetail -> FILM_DETAIL
        }
    }

    override fun onBindViewHolder(holder: SelectedDetailsViewHolder, position: Int) {

        holder.binding.containerSelectedItem.setOnClickListener {
            var isSelected = false
            if (holder.binding.btnSelectDetailRv.visibility == View.VISIBLE)
                isSelected = true
            if (clickListener.selectDetail(getItem(position)!!, isSelected))
                holder.binding.btnSelectDetailRv.visibility = View.VISIBLE
            else
                holder.binding.btnSelectDetailRv.visibility = View.GONE
        }

        holder.fillViewItems(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedDetailsViewHolder {
        return SelectedDetailsViewHolder.from(parent, viewType)
    }

    class SelectedDetailsViewHolder private constructor(val binding: SelectedDetailItemRvBinding, private val viewType: Int) :
        RecyclerView.ViewHolder(binding.root), KoinComponent {

        private val androidTempFileProvider: TempFileProvider by inject()

        fun fillViewItems(
            detail: Detail,
            clickListener: SelectedDetailsClickListener
        ) {
            binding.detailImg.background = null
            binding.detail = detail
            binding.clickListener = clickListener

            when (viewType) {
                AUDIO_DETAIL -> (Glide.with(binding.detailImg).load(R.drawable.event_detail_audio).into(binding.detailImg))
                PICTURE_DETAIL -> (Glide.with(binding.detailImg).load(androidTempFileProvider.getFile(binding.detail!! as PhotoDetail)).signature
                            (MediaStoreSignature("", binding.detail!!.file.lastModified(), 0)).into(binding.detailImg))
                DRAW_DETAIL -> (Glide.with(binding.detailImg).load(androidTempFileProvider.getFile(binding.detail!! as DrawingDetail)).signature(
                                MediaStoreSignature("", binding.detail!!.file.lastModified(), 0)).into(binding.detailImg))
                TEXT_DETAIL -> (Glide.with(binding.detailImg).load(R.drawable.event_detail_text).into(binding.detailImg))
                VIDEO_DETAIL -> (Glide.with(binding.detailImg).load(R.drawable.event_detail_camera).into(binding.detailImg))
                YOUTUBE_DETAIL -> {
                    detail as YoutubeVideoDetail
                    Glide.with(binding.detailImg).load(getDefaultThumbnailUrl(detail.videoId)).into(binding.detailImg)
                } }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewType: Int): SelectedDetailsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SelectedDetailItemRvBinding.inflate(layoutInflater, parent, false)
                return SelectedDetailsViewHolder(binding, viewType)
            }
        }
    }
}

class SelectedDetailsDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldDetail: Detail, newDetail: Detail): Boolean {
        return oldDetail.uuid == newDetail.uuid
    }

    override fun areContentsTheSame(oldDetail: Detail, newDetail: Detail): Boolean {
        return oldDetail.uuid == newDetail.uuid
    }
}

interface SelectedDetailsClickListener {
    fun selectDetail(detail: Detail, isSelected: Boolean): Boolean
}