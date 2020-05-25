package be.hogent.faith.faith.skyscraper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.skyscraper_base
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.txt_goal_description
import org.koin.core.KoinComponent
import be.hogent.faith.faith.skyscraper.SkyscraperViewHolder.SkyscraperNavigationListener

object SkyscraperViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        skyscraperNavigationListener: SkyscraperNavigationListener
    ): SkyscraperViewHolder {

        val view: ConstraintLayout
        when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_blue,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_YELLOW -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_yellow,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_PINK -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_DARK_GREEN -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_GREEN -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_green,
                    parent,
                    false
                ) as ConstraintLayout
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_blue,
                    parent,
                    false
                ) as ConstraintLayout
            }
        }
        return SkyscraperViewHolder(view, skyscraperNavigationListener)
    }
}

class SkyscraperViewHolder(
    val view: ConstraintLayout,
    private val skyscraperNavigationListener: SkyscraperNavigationListener
) : RecyclerView.ViewHolder(view), KoinComponent {


    fun bind(skyscraper: Skyscraper) {
        view.txt_goal_description.text =
            if (skyscraper.description.length < 60) skyscraper.description else "${skyscraper.description.substring(
                0,
                60
            )}..."

        view.skyscraper_base.setOnClickListener {
            skyscraperNavigationListener.openGoalScreenFor(skyscraper)
        }
    }

    interface SkyscraperNavigationListener {
        fun openGoalScreenFor(skyscraper: Skyscraper)
        fun deleteSkyscraper(skyscraper: Skyscraper)
    }
}

