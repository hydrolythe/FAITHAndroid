package be.hogent.faith.faith.mainScreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.R
import be.hogent.faith.faith.ui.MainActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenFragmentTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Before
    fun goToScreen() {
        NavigationUtil.goToCityScreen()
    }

    @Test
    fun mainScreenFragment_buttonsOpenCorrectScreen() {
        onView(withId(R.id.main_first_location)).perform(click())
//        pressBack()

        onView(withId(R.id.main_second_location)).perform(click())
        onView(withId(R.id.screen_new_event)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(R.id.main_third_location)).perform(click())
        onView(withId(R.id.screen_overview_events)).check(matches(isDisplayed()))
        pressBack()
    }
}