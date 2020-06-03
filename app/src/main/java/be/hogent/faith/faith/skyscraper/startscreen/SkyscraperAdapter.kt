package be.hogent.faith.faith.skyscraper.startscreen

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors.SKYSCRAPER_BLUE
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors.SKYSCRAPER_DARK_GREEN
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors.SKYSCRAPER_GREEN
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors.SKYSCRAPER_PINK
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors.SKYSCRAPER_YELLOW
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperViewHolder.SkyscraperNavigationListener


enum class SkyscraperColors(val value: Int) {
    SKYSCRAPER_BLUE(1) ,
    SKYSCRAPER_YELLOW(2),
    SKYSCRAPER_PINK(3),
    SKYSCRAPER_DARK_GREEN(4),
    SKYSCRAPER_GREEN(5)
}
class SkyscraperAdapter(
    private val skyscraperNavigationListener: SkyscraperNavigationListener,
    private val skyscraperClickListener: SkyscraperClickListener
) : ListAdapter<Skyscraper, SkyscraperViewHolder>(
    SkyscraperDiffCallback()
) {

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
            0 -> SKYSCRAPER_BLUE.value
            1 -> SKYSCRAPER_YELLOW.value
            2 -> SKYSCRAPER_PINK.value
            3 -> SKYSCRAPER_DARK_GREEN.value
            4 -> SKYSCRAPER_GREEN.value
            else -> SKYSCRAPER_BLUE.value
        }
    }

    override fun onBindViewHolder(holder: SkyscraperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class Skyscraper(
    var description: String,
    var color : SkyscraperColors
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