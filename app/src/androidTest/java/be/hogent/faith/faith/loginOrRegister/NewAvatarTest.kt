package be.hogent.faith.faith.loginOrRegister

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewAvatarTest {

    @get:Rule
    var activityScenarioRule = ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Test
    fun checkCanEnterName() {
        closeSoftKeyboard()
        onView(withId(R.id.txt_welcome_userName)).perform(typeText("Reinhard"))
        closeSoftKeyboard()
    }

    /*
    @Test
    fun registerAvatarFragment_noNameEntered_showsErrorWhenGoingToCity() {
        onView(withId(R.id.btn_welcome_login)).perform(click())
        ToastMatcher.isToastMessageDisplayed(R.string.txt_error_userNameOrAvatarNotSet)
    }
    */
}