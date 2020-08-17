package be.hogent.faith.faith.skyscraper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.skyscraper.goal.SkyscraperGoalFragment
import be.hogent.faith.faith.skyscraper.history.OverviewGoalDialog
import be.hogent.faith.faith.skyscraper.history.SkyscraperHistoryFragment
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperStartScreenFragment
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin

/**
 * An activity representing a list of Events of the user.
 */
class SkyscraperActivity : AppCompatActivity(), SkyscraperViewHolder.SkyscraperNavigationListener,
    SkyscraperStartScreenFragment.SkyscraperNavigationListener,
    SkyscraperGoalFragment.SkyscraperNavigationListener,
    SkyscraperHistoryFragment.SkyscraperNavigationListener,
    SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = SkyscraperStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun openGoalScreenFor(goal: Goal) {
        replaceFragment(SkyscraperGoalFragment.newInstance(goal), R.id.fragment_container)
    }

    override fun openSkyscrapersHistory() {
        replaceFragment(SkyscraperHistoryFragment.newInstance(), R.id.fragment_container)
    }

    override fun deleteSkyscraper(goal: Goal) {
        TODO("Not yet implemented")
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

    override fun closeSkyscrapers() {
        finish()
    }

    override fun openSkyscraperHistoryScreenFor(goal: Goal) {
        val overviewGoalDialog = OverviewGoalDialog.newInstance(goal)
        overviewGoalDialog.show(supportFragmentManager, null)
    }
}