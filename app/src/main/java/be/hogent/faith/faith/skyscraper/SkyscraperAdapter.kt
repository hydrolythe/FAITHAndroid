package be.hogent.faith.faith.skyscraper

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import be.hogent.faith.faith.skyscraper.SkyscraperColors.SKYSCRAPER_BLUE
import be.hogent.faith.faith.skyscraper.SkyscraperColors.SKYSCRAPER_DARK_GREEN
import be.hogent.faith.faith.skyscraper.SkyscraperColors.SKYSCRAPER_GREEN
import be.hogent.faith.faith.skyscraper.SkyscraperColors.SKYSCRAPER_PINK
import be.hogent.faith.faith.skyscraper.SkyscraperColors.SKYSCRAPER_YELLOW
import be.hogent.faith.faith.skyscraper.SkyscraperViewHolder.SkyscraperNavigationListener

object SkyscraperColors {
    const val SKYSCRAPER_BLUE = 1
    const val SKYSCRAPER_YELLOW = 2
    const val SKYSCRAPER_PINK = 3
    const val SKYSCRAPER_DARK_GREEN = 4
    const val SKYSCRAPER_GREEN = 5
}

class SkyscraperAdapter(
    private val skyscraperNavigationListener: SkyscraperNavigationListener,
    private val skyscraperClickListener: SkyscraperClickListener
) : ListAdapter<Skyscraper, SkyscraperViewHolder>(SkyscraperDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkyscraperViewHolder {
        return SkyscraperViewHolderFactory.createViewHolder(
            parent,
            viewType,
            skyscraperNavigationListener,
            skyscraperClickListener
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> SKYSCRAPER_BLUE
            1 -> SKYSCRAPER_YELLOW
            2 -> SKYSCRAPER_PINK
            3 -> SKYSCRAPER_DARK_GREEN
            4 -> SKYSCRAPER_GREEN
            else -> SKYSCRAPER_BLUE
        }
    }

    override fun onBindViewHolder(holder: SkyscraperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class Skyscraper(
    var description: String
)

class SkyscraperDiffCallback : DiffUtil.ItemCallback<Skyscraper>() {
    override fun areItemsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Skyscraper, newItem: Skyscraper): Boolean {
        return oldItem == newItem
    }
}

interface SkyscraperClickListener {
    fun getSelectedSkyscraper(layout: ConstraintLayout, position: Int)
}