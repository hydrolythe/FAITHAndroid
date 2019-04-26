package be.hogent.faith.faith.enterEventDetails

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.ui.MainActivity
import be.hogent.faith.faith.util.NavigationUtil
import be.hogent.faith.faith.util.ToastMatcher
import be.hogent.faith.faith.util.allowPermissionsIfNeeded
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDetailsFragmentTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var uiDevice: UiDevice

    @Before
    fun goToScreen() {
        NavigationUtil.goToNewEventScreen()
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun eventDetailsFragment_buttonsOpenCorrectScreen() {
        onView(withId(be.hogent.faith.R.id.btn_event_details_audio)).perform(click())
        allowPermissionsIfNeeded(uiDevice)
        onView(withId(be.hogent.faith.R.id.screen_record_audio)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(be.hogent.faith.R.id.btn_event_details_camera)).perform(click())
        allowPermissionsIfNeeded(uiDevice)
        onView(withId(be.hogent.faith.R.id.screen_take_photo)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(be.hogent.faith.R.id.btn_event_details_drawing)).perform(click())
        // TODO: Enable once drawing has been implemented
//        pressBack()

        onView(withId(be.hogent.faith.R.id.btn_event_details_text)).perform(click())
        // TODO: Enable once text has been implemented
//        pressBack()

        onView(withId(be.hogent.faith.R.id.btn_event_details_gotoEmotionAvatar)).perform(click())
        onView(withId(be.hogent.faith.R.id.screen_draw_avatar)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(be.hogent.faith.R.id.btn_event_details_send)).perform(click())
        onView(withId(be.hogent.faith.R.id.screen_save_event)).check(matches(isDisplayed()))
        pressBack()
    }

    @Test
    fun eventDetailsFragment_enterEventTitle() {
        onView(withId(R.id.txt_event_details_event_title)).perform(typeText("Titel van Event"))
    }

    @Test
    fun eventDetailsFragment_saveEventWithoutTitleEntered_errorMessage() {
        onView(withId(be.hogent.faith.R.id.btn_event_details_send)).perform(click())
        onView(withId(be.hogent.faith.R.id.btn_save_event_save)).perform(click())
        ToastMatcher.isToastMessageDisplayed(R.string.toast_event_no_title)
    }

    @Test
    fun eventDetailsFragment_saveEvent_showsSuccess() {
        onView(withId(be.hogent.faith.R.id.btn_event_details_send)).perform(click())
        onView(withId(be.hogent.faith.R.id.txt_save_event_title)).perform(typeText("Titel van Event"))
        onView(withId(be.hogent.faith.R.id.txt_save_event_notes)).perform(typeText("Notities notities notities"))
        closeSoftKeyboard()
        onView(withId(be.hogent.faith.R.id.btn_save_event_save)).perform(click())
        ToastMatcher.isToastMessageDisplayed(R.string.toast_save_event_success)
    }
}
