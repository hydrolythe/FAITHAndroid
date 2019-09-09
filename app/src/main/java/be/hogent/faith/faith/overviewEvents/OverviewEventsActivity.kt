package be.hogent.faith.faith.overviewEvents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_drawer_layout
import kotlinx.android.synthetic.main.activity_overview_events.overviewEvents_nav_view

class OverviewEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview_events)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = OverviewEventsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.overviewEvents_fragment_container, fragment)
                .commit()
        }

        overviewEvents_nav_view.setNavigationItemSelectedListener { menuItem ->
            // close drawer when item is tapped
            emotionCapture_drawer_layout.closeDrawers()
            // perform action
            when (menuItem.itemId) {
                R.id.nav_city -> {
                    finish()
                }
            }
            true
        }
    }
}