package be.hogent.faith.faith.skyscraper.startscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.Goal
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

        val skyscraperLayoutFile = when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE.value -> R.layout.skyscraper_rv_blue
            SkyscraperColors.SKYSCRAPER_YELLOW.value -> R.layout.skyscraper_rv_yellow
            SkyscraperColors.SKYSCRAPER_RED.value -> R.layout.skyscraper_rv_red
            SkyscraperColors.SKYSCRAPER_DARK_GREEN.value -> R.layout.skyscraper_rv_dark_green
            SkyscraperColors.SKYSCRAPER_GREEN.value -> R.layout.skyscraper_rv_green
            else -> R.layout.skyscraper_rv_blue
        }

        val view: ConstraintLayout = LayoutInflater.from(parent.context)
            .inflate(skyscraperLayoutFile, parent, false) as ConstraintLayout

        return SkyscraperViewHolder(view, skyscraperNavigationListener, skyscraperClickListener)
    }
}

class SkyscraperViewHolder(
    val view: ConstraintLayout,
    private val skyscraperNavigationListener: SkyscraperNavigationListener,
    private val skyscraperClickListener: SkyscraperClickListener
) : RecyclerView.ViewHolder(view), KoinComponent {

    fun bind(goal: Goal) {
        view.txt_goal_description.setText(goal.description)

        view.skyscraper_base.setOnClickListener {
            skyscraperNavigationListener.openGoalScreenFor(goal)
        }

        view.txt_goal_description.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                skyscraperClickListener.getSelectedSkyscraper(view, this.layoutPosition)
            }
        }
    }

    interface SkyscraperNavigationListener {
        fun openGoalScreenFor(goal: Goal)
        fun deleteSkyscraper(goal: Goal)
    }
}
