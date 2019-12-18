package be.hogent.faith.faith.emotionCapture.editDetail

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import be.hogent.faith.faith.util.ToastMatcher
import be.hogent.faith.faith.util.hasTextInputLayoutHintText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailFragmentWithEmotionAvatarTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToNewEventScreen()
    }

    @Test
    fun editDetailsFragment_saveEventWithoutTitleEntered_errorMessage() {
        onView(withId(R.id.btn_event_send)).perform(ViewActions.click())
        onView(withId(R.id.btn_save_event_save)).perform(ViewActions.click())
        onView(withId(R.id.textInputLayout_eventtitle)).check(matches(hasTextInputLayoutHintText(activityScenarioRule.activity.getString(R.string
            .error_event_no_title))))
    }

    @Test
    fun editDetailsFragment_saveEvent_showsSuccess() {
        onView(withId(R.id.btn_event_send)).perform(ViewActions.click())
        onView(withId(R.id.txt_save_event_title))
            .perform(ViewActions.typeText("Titel van Event"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.txt_save_event_notes))
            .perform(ViewActions.typeText("Notities notities notities"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.btn_save_event_save))
            .perform(ViewActions.click())
        // Commented out the following line because UI tests on Firebase Testlab are failing
        // although on local emulator no problems arise
        ToastMatcher.isToastMessageDisplayed(R.string.save_event_success)
    }
}