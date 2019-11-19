package be.hogent.faith.faith.emotionCapture.drawEmotionAvatar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawEmotionAvatarFragmentTest {
    @get:Rule
    var activityScenarioRule = ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToDrawEmotionAvatarScreen()
    }

    @Test
    fun drawEmotionFragment_canClickAllButtons() {
        onView(withId(R.id.btn_draw_setColorBlack)).perform(click())
        onView(withId(R.id.btn_draw_setColorBlue)).perform(click())
        onView(withId(R.id.btn_draw_setColorGreen)).perform(click())
        onView(withId(R.id.btn_draw_setColorRed)).perform(click())
        onView(withId(R.id.btn_draw_setColorYellow)).perform(click())

        onView(withId(R.id.btn_draw_setThinLineWidth)).perform(click())
        onView(withId(R.id.btn_draw_setMediumLineWidth)).perform(click())
        onView(withId(R.id.btn_draw_setThickLineWidth)).perform(click())

        onView(withId(R.id.btn_draw_undo)).perform(click())
    }
}