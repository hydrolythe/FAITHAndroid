package be.hogent.faith.faith.skyscraper.startscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.GoalColor
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperViewHolder.SkyscraperNavigationListener
import com.jakewharton.rxbinding4.widget.afterTextChangeEvents
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.skyscraper_base
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.skyscraper_panel
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.txt_goal_description
import org.koin.core.KoinComponent
import java.util.concurrent.TimeUnit

object SkyscraperViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        skyscraperNavigationListener: SkyscraperNavigationListener,
        skyscraperPanelTextListener: SkyscraperPanelTextListener
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

        return SkyscraperViewHolder(view, skyscraperNavigationListener, skyscraperPanelTextListener)
    }
}

class SkyscraperViewHolder(
    val view: ConstraintLayout,
    private val skyscraperNavigationListener: SkyscraperNavigationListener,
    private val skyscraperPanelTextListener: SkyscraperPanelTextListener
) : RecyclerView.ViewHolder(view), KoinComponent {
    private var disposables = io.reactivex.rxjava3.disposables.CompositeDisposable()

    fun bind(goal: Goal) {
        val baseDrawable = when (goal.goalColor) {
            GoalColor.GREEN -> R.drawable.skyscraper_green_base
            GoalColor.RED -> R.drawable.skyscraper_red_base
            GoalColor.YELLOW -> R.drawable.skyscraper_yellow_base
            GoalColor.BLUE -> R.drawable.skyscraper_blue_base
            GoalColor.PURPLE -> R.drawable.skyscraper_darkgreen_base
        }
        val panelDrawable = when (goal.goalColor) {
            GoalColor.GREEN -> R.drawable.skyscraper_green_panel
            // TODO: change to red once drawable is available
            GoalColor.RED -> R.drawable.skyscraper_blue_panel
            GoalColor.YELLOW -> R.drawable.skyscraper_yellow_panel
            GoalColor.BLUE -> R.drawable.skyscraper_blue_panel
            GoalColor.PURPLE -> R.drawable.skyscraper_darkgreen_panel
        }

        view.skyscraper_base.setImageResource(baseDrawable)
        view.skyscraper_panel.setImageResource(panelDrawable)
        view.txt_goal_description.setText(goal.description)
        view.txt_goal_description.tag = goal

        view.skyscraper_base.setOnClickListener {
            skyscraperNavigationListener.openGoalScreenFor(goal)
        }

        disposables.add(view.txt_goal_description.afterTextChangeEvents()
            .skip(1)
            .debounce(1, TimeUnit.SECONDS)
            .map { skyscraperPanelTextListener.onPanelTextChanged(goal, it.editable.toString()) }
            .subscribe())
    }

    interface SkyscraperNavigationListener {
        fun openGoalScreenFor(goal: Goal)
        fun deleteSkyscraper(goal: Goal)
    }
}
