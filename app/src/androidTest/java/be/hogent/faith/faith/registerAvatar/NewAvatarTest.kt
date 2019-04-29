package be.hogent.faith.faith.registerAvatar

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewAvatarTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java)

    @Test
    fun checkCanEnterName() {
        closeSoftKeyboard()
        onView(withId(R.id.avatar_txt_name)).perform(typeText("Reinhard"))
        closeSoftKeyboard()
    }
}