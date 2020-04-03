package be.hogent.faith.faith.cityScreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class CityScreenFragmentTest : KoinTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToCityScreenViaRegistration()
    }

    @Test
    fun mainScreenFragment_buttonsOpenCorrectScreen() {
        // TODO: wat moet hier getest worden?
        //onView(withId(R.id.btn_loginfragment_startNewEvent)).check(matches(isDisplayed()))
        //Thread.sleep(3000)
        //onView(withId(R.id.image_main_avatar)).check(matches(isDisplayed()))

        //TODO Wat moet hier getest worden?
        //onView(withId(R.id.btn_loginfragment_startNewEvent)).perform(click())
        //Thread.sleep(3000)
        //onView(withId(R.id.screen_new_event)).check(matches(isDisplayed()))
        //pressBack()
    }
}