package be.hogent.faith.faith.loginOrRegister

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class LoginTest : KoinTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun setUp() {
    }

    @Test
    fun canLogin() {
        closeSoftKeyboard()
        onView(withId(R.id.txt_welcome_userName)).perform(typeText(UUID.randomUUID().toString()))
        closeSoftKeyboard()
        onView(withId(R.id.txt_welcome_password)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_register_naar_de_stad)).perform(ViewActions.click())
        // Just for safety
        closeSoftKeyboard()
        onView(withId(R.id.btn_loginfragment_library)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))
    }

    /*
    @Test
    fun registerAvatarFragment_noNameEntered_showsErrorWhenGoingToCity() {
        onView(withId(R.id.btn_welcome_login)).perform(click())
        ToastMatcher.isToastMessageDisplayed(R.string.txt_error_userNameOrAvatarNotSet)
    }
    */
}