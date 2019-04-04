package be.hogent.faith.faith.takePhoto

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.ui.MainActivity
import be.hogent.faith.faith.util.NavigationUtil
import be.hogent.faith.faith.util.allowPermissionsIfNeeded
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TakePhotoFragmentTest {
    private lateinit var uiDevice: UiDevice

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)


    @Before
    fun goToScreen() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        NavigationUtil.goToTakePhotoScreen()
        allowPermissionsIfNeeded(uiDevice)
    }

    @Test
    fun takePhotoFragment_takePhoto_enterName_OK() {
        onView(withId(R.id.btn_takePhoto_takePhoto)).perform(click())
        onView(withId(R.id.txt_save_photo_name)).perform(typeText("Photo name"))
        onView(withId(R.id.btn_save_photo_save)).perform(click())
    }

}