package be.hogent.faith.faith.emotionCapture.takePhoto

import androidx.test.espresso.Espresso.onView
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

@RunWith(AndroidJUnit4::class)
class TakePhotoFragmentTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToTakePhotoScreen()
    }

    @Test
    fun makeDrawingScreen_saveClicked_backToPark() {
        onView(withId(R.id.btn_takePhoto_takePhoto)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.btn_takePhoto_Ok)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.screen_new_event)).check(matches(isDisplayed()))
    }
}
