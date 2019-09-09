package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import be.hogent.faith.faith.util.RecyclerViewItemCountAssertion
import be.hogent.faith.faith.util.allowPermissionsIfNeeded
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsAreAddedToBottom {

    private lateinit var uiDevice: UiDevice

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun setUp() {
        NavigationUtil.goToNewEventScreen()
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun eventDetails_newPhotoIsAddedToBottomList() {
        // open camera
        onView(withId(R.id.btn_event_details_camera)).perform(click())
        allowPermissionsIfNeeded(uiDevice)
        // take picture
        onView(withId(R.id.btn_takePhoto_takePhoto)).perform(click())
        // Wait for picture to be saved
        // TODO: replace sleep with IdlingResource implementation
        // See https://developer.android.com/reference/androidx/test/espresso/idling/CountingIdlingResource.html
        Thread.sleep(3000)
        onView(withId(R.id.btn_takePhoto_Ok)).perform(click())
        Thread.sleep(3000)
        // back to overview
        pressBack()
        // Check if thumbnail was added
        onView(withId(R.id.recyclerView_event_details_details)).check(RecyclerViewItemCountAssertion(1))
    }
}