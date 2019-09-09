package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import be.hogent.faith.faith.util.allowPermissionsIfNeeded
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDetailsFragmentTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    private lateinit var uiDevice: UiDevice

    @Before
    fun goToScreen() {
        NavigationUtil.goToNewEventScreen()
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun eventDetailsFragment_buttonsOpenCorrectScreen() {
        // Audio is not tested as emulator don't support audio.
        onView(withId(R.id.btn_event_details_camera)).perform(click())
        allowPermissionsIfNeeded(uiDevice)
        onView(withId(R.id.screen_take_photo)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(R.id.btn_event_details_drawing)).perform(click())
        onView(withId(R.id.screen_draw)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(R.id.btn_event_details_text)).perform(click())
        pressBack()
    }

    @Test
    fun eventDetailsFragment_enterEventTitle() {
        onView(withId(R.id.txt_event_details_title)).perform(typeText("Titel van Event"))
        closeSoftKeyboard()
    }
}
