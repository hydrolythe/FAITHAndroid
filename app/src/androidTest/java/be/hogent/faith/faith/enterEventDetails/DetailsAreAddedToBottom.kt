package be.hogent.faith.faith.enterEventDetails

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.ui.MainActivity
import be.hogent.faith.faith.util.RecyclerViewItemCountAssertion
import be.hogent.faith.faith.util.allowPermissionsIfNeeded
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsAreAddedToBottom {
    private lateinit var uiDevice: UiDevice

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun eventDetails_newPhotoIsAddedToBottomList() {
        // go to event details screen
        onView(withId(R.id.main_second_location)).perform(click())
        // open photoTaker
        onView(withId(R.id.btn_event_details_camera)).perform(click())
        allowPermissionsIfNeeded(uiDevice)
        // take picture
        onView(withId(R.id.btn_takePhoto_takePhoto)).perform(click())
        // Required to let picture get saved correctly
        // TODO: find a way around this. Possibly block the back button until everything was saved? -> Bad UX
        // Tip: https://medium.com/@elye.project/instrumental-test-better-espresso-without-sleep-d3391b19a581
        Thread.sleep(3000)
        // back to overview
        pressBack()
        // Check if thumbnail was added
        onView(withId(R.id.recyclerView_event_details_details)).check(RecyclerViewItemCountAssertion(1))
    }
}