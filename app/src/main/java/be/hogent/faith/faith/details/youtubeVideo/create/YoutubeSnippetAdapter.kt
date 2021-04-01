package be.hogent.faith.faith.details.youtubeVideo.create

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.YoutubeSnippetItemRvBinding
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.getHighQualityThumbnailUrl
import com.bumptech.glide.Glide

class YoutubeSnippetAdapter(private val context: Context?, private val clickListener: SnippetClickListener) :
    ListAdapter<YoutubeVideoDetail, YoutubeSnippetAdapter.SnippetViewHolder>(
        SnippetDiffCallback()
    ) {

    override fun onBindViewHolder(holder: SnippetViewHolder, position: Int) {
        holder.binding.containerSnippet.setOnClickListener {
            clickListener.onSnippetClick(getItem(position)!!)
        }

        holder.fillViewItems(getItem(position)!!, clickListener)
        animateViewElements(holder)
    }

    private fun animateViewElements(holder: SnippetViewHolder) {
        holder.binding.containerSnippet.animation =
            AnimationUtils.loadAnimation(context, R.xml.videothumbs_slide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnippetViewHolder {
        return SnippetViewHolder.from(
            parent
        )
    }

    class SnippetViewHolder private constructor(val binding: YoutubeSnippetItemRvBinding) : RecyclerView.ViewHolder(binding.root) {

        fun fillViewItems(
            snippet: YoutubeVideoDetail,
            clickListener: SnippetClickListener
        ) {
            binding.imgSnippetThumbnail.background = null
            Glide.with(binding.imgSnippetThumbnail).load(getHighQualityThumbnailUrl(snippet.videoId)).into(binding.imgSnippetThumbnail)
            binding.snippet = snippet
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SnippetViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = YoutubeSnippetItemRvBinding.inflate(layoutInflater, parent, false)
                return SnippetViewHolder(
                    binding
                )
            }
        }
    }
}

class SnippetDiffCallback : DiffUtil.ItemCallback<YoutubeVideoDetail>() {
    override fun areItemsTheSame(oldItem: YoutubeVideoDetail, newItem: YoutubeVideoDetail): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(oldItem: YoutubeVideoDetail, newItem: YoutubeVideoDetail): Boolean {
        return oldItem.videoId == newItem.videoId
    }
}

interface SnippetClickListener {
    fun onSnippetClick(snippet: YoutubeVideoDetail)
}