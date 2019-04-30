package be.hogent.faith.faith.emotionCapture.drawing.drawEmotionAvatar

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawEmotionAvatarFragmentTest {
    @Test
    fun drawEmotionFragment_launches() {
        launchFragmentInContainer<DrawEmotionAvatarFragment>()
    }

    @Test
    fun drawEmotionFragment_canClickAllButtons() {
        launchFragmentInContainer<DrawEmotionAvatarFragment>()

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