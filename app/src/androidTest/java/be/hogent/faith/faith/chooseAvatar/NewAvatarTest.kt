package be.hogent.faith.faith.chooseAvatar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.R
import be.hogent.faith.faith.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewAvatarTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Before
    fun goToScreen() {
        // NOP: new Avatar is default screen
    }

    @Test
    fun newAvatar_canClickAllButtons() {
        // TODO: try all buttons
    }

    @Test
    fun checkCanEnterName() {
        onView(withId(R.id.avatar_txt_name)).perform(typeText("Reinhard"))
    }
}