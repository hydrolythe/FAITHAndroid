package be.hogent.faith.faith.loginOrRegister

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class RegisterTest : KoinTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun setUp() {
    }

    @Test
    fun canRegister() {
        onView(withId(R.id.btn_login_ik_ben_nieuw)).perform(ViewActions.click())

        closeSoftKeyboard()
        onView(withId(R.id.txt_register_username)).perform(typeText(UUID.randomUUID().toString()))
        closeSoftKeyboard()
        onView(withId(R.id.txt_register_password)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.txt_register_password_repeat)).perform(typeText("wwwwww"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_register_confirmInfo)).perform(ViewActions.click())
        onView(withId(R.id.avatar_rv_avatar)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AvatarItemAdapter.ViewHolder>(
                0,
                ViewActions.click()
            )
        )
        onView(withId(R.id.btn_register_naar_de_stad)).perform(ViewActions.click())
        // Just for safety
        closeSoftKeyboard()
        onView(withId(R.id.btn_loginfragment_library)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}