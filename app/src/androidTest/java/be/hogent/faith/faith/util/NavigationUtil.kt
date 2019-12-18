package be.hogent.faith.faith.util

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.R
import be.hogent.faith.database.di.databaseModule
import be.hogent.faith.faith.androidTestModule
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter
import be.hogent.faith.service.usecases.di.serviceModule
import be.hogent.faith.storage.di.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import java.util.UUID

object NavigationUtil {
    fun startFaithApp() {
        koinApplication {
            androidContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext()
            ) //ApplicationProvider.getApplicationContext())
            modules(
                listOf(
                    appModule,
                    databaseModule,
                    serviceModule,
                    storageModule,
                    androidTestModule
                )
            )
        }
    }

    fun goToCityScreen() {
        // Focus is on input field for the name by default, which opens the soft keyboard.
        // It hides the button to go to town so we have to close it.
        // We have to close the soft keyboard everytime because otherwise it overlaps the textfield.
        onView(withId(R.id.btn_loginfragment_library)).perform(click())

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
        onView(withId(R.id.btn_register_finishRegistration)).perform(click())
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
        goToCityScreen()
        onView(withId(R.id.btn_loginfragment_library)).perform(click())
        // Just for safety
        closeSoftKeyboard()
    }

    fun goToNewEventScreen() {
        goToCityScreen()
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