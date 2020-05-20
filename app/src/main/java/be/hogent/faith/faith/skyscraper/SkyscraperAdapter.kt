package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperRvBlueBinding
import be.hogent.faith.databinding.SkyscraperRvDarkGreenBinding
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperColors.SKYSCRAPER_BLUE
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperColors.SKYSCRAPER_DARK_GREEN
import be.hogent.faith.faith.skyscraper.SkyscraperViewHolder
import be.hogent.faith.faith.skyscraper.SkyscraperViewholderFactory


object SkyscraperColors {
    const val SKYSCRAPER_BLUE = 1
    const val SKYSCRAPER_DARK_GREEN = 2

}



class SkyscraperAdapter : ListAdapter<Skyscraper, SkyscraperViewHolder>(SkyscraperDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkyscraperViewHolder {
        return SkyscraperViewholderFactory.createViewHolder(
            parent,
            viewType
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> SKYSCRAPER_BLUE
            1 -> SKYSCRAPER_DARK_GREEN
            else -> SKYSCRAPER_BLUE
        }
    }

    override fun onBindViewHolder(holder: SkyscraperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


data class Skyscraper(
    val description: String
)

class SkyscraperDiffCallback : DiffUtil.ItemCallback<Skyscraper>() {
        override fun areItemsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
            return oldItem.description == newItem.description
        }

        override fun areContentsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
            return oldItem == newItem
        }

}