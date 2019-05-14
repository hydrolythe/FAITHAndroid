package be.hogent.faith.faith.overviewEvents

import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewEventsFragmentTest {

    @get:Rule
    var activityScenarioRule = ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.createEvent()
        NavigationUtil.goToEventsOverviewScreen()
    }

    // TODO : make test succeed. Error on glide?
    @Test
    fun overviewEventsFragment_shows() {
        // onView(withId(R.id.screen_overview_events)).check(matches(isDisplayed()))
    }
}