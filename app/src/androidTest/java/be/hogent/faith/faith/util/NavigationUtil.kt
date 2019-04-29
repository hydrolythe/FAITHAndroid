package be.hogent.faith.faith.util

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import be.hogent.faith.R
import be.hogent.faith.faith.registerAvatar.AvatarItemAdapter

object NavigationUtil {
    fun goToCityScreen() {
        // Focus is on input field for the name by default, which opens the soft keyboard.
        // It hides the button to go to town so we have to close it.
        closeSoftKeyboard()
        onView(withId(R.id.txt_avatar_name)).perform(typeText("TestNaam"))
        closeSoftKeyboard()
        onView(withId(R.id.avatar_rv_avatar)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AvatarItemAdapter.ViewHolder>(
                0,
                click()
            )
        )
        onView(withId(R.id.btn_avatar_go_to_town)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToEventsOverviewScreen() {
        goToCityScreen()
        onView(withId(R.id.main_third_location)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToNewEventScreen() {
        goToCityScreen()
        onView(withId(R.id.main_second_location)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToTakePhotoScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_camera)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToRecordAudioScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_audio)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToWriteTextScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_text)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToDrawEmotionAvatarScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_gotoEmotionAvatar)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToMakeDrawingScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_drawing)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }
}