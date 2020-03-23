package be.hogent.faith.faith.util

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter
import java.util.UUID

object NavigationUtil {
    fun goToCityScreenViaRegistration() {
        // Focus is on input field for the name by default, which opens the soft keyboard.
        // It hides the button to go to town so we have to close it.
        // We have to close the soft keyboard everytime because otherwise it overlaps the textfield.
        onView(withId(R.id.btn_login_ik_ben_nieuw)).perform(click())

        closeSoftKeyboard()
        onView(withId(R.id.txt_register_username)).perform(typeText(UUID.randomUUID().toString()))
        closeSoftKeyboard()
        onView(withId(R.id.txt_register_password)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.txt_register_password_repeat)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_register_confirmInfo)).perform(click())
        onView(withId(R.id.avatar_rv_avatar)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AvatarItemAdapter.ViewHolder>(
                0,
                click()
            )
        )
        onView(withId(R.id.btn_register_naar_de_stad)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToCityScreenViaLogin() {
        // Focus is on input field for the name by default, which opens the soft keyboard.
        // It hides the button to go to town so we have to close it.
        // We have to close the soft keyboard everytime because otherwise it overlaps the textfield.
        closeSoftKeyboard()
        onView(withId(R.id.txt_welcome_userName)).perform(typeText("jan"))
        closeSoftKeyboard()
        onView(withId(R.id.txt_welcome_password)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_register_naar_de_stad)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun createEvent() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_send)).perform(click())
        onView(withId(R.id.txt_save_event_title)).perform(typeText("Titel van Event"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_save_event_save)).perform(click())
    }

    fun goToEventsOverviewScreen() {
        goToCityScreenViaRegistration()
        onView(withId(R.id.btn_loginfragment_library)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToNewEventScreen() {
        goToCityScreenViaLogin()
        Thread.sleep(3000)
        onView(withId(R.id.btn_loginfragment_startNewEvent)).perform(click())
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
        onView(withId(R.id.img_event_details_avatar_inkleuren)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToMakeDrawingScreen() {
        goToNewEventScreen()
        onView(withId(R.id.btn_event_details_drawing)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }
    fun goToBackpack() {
        goToCityScreenViaLogin()
        onView(withId(R.id.btn_backpack)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }
}