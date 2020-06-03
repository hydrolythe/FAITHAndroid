package be.hogent.faith.faith.skyscraper.startscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperViewHolder.SkyscraperNavigationListener
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.skyscraper_base
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.txt_goal_description
import org.koin.core.KoinComponent

object SkyscraperViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        skyscraperNavigationListener: SkyscraperNavigationListener,
        skyscraperClickListener: SkyscraperClickListener
    ): SkyscraperViewHolder {

        val view: ConstraintLayout
        when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE.value -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_blue,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_YELLOW.value -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_yellow,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_PINK.value -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_DARK_GREEN.value -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
            }
            SkyscraperColors.SKYSCRAPER_GREEN.value -> {
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
        return SkyscraperViewHolder(
            view,
            skyscraperNavigationListener,
            skyscraperClickListener
        )
    }
}

class SkyscraperViewHolder(
    val view: ConstraintLayout,
    private val skyscraperNavigationListener: SkyscraperNavigationListener,
    private val skyscraperClickListener: SkyscraperClickListener
) : RecyclerView.ViewHolder(view), KoinComponent {

    fun bind(skyscraper: Skyscraper) {
        view.txt_goal_description.setText(skyscraper.description)

        view.skyscraper_base.setOnClickListener {
            skyscraperNavigationListener.openGoalScreenFor(skyscraper)
        }

        view.txt_goal_description.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                skyscraperClickListener.getSelectedSkyscraper(view, this.layoutPosition)
            }
        }
    }

    interface SkyscraperNavigationListener {
        fun openGoalScreenFor(skyscraper: Skyscraper)
        fun deleteSkyscraper(skyscraper: Skyscraper)
    }
}
