package be.hogent.faith.faith.cityScreen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CityScreenFragmentTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToCityScreen()
    }

    // TODO: enable once FAITH-143 is fixed
//    @Test
//    fun mainScreenFragment_buttonsOpenCorrectScreen() {
//        onView(withId(R.id.btn_archive)).perform(click())
//        onView(withId(R.id.screen_overview_events)).check(matches(isDisplayed()))
//        pressBack()
//
//        onView(withId(R.id.btn_welcome_login)).perform(click())
//        onView(withId(R.id.screen_new_event)).check(matches(isDisplayed()))
//        pressBack()
//    }
}