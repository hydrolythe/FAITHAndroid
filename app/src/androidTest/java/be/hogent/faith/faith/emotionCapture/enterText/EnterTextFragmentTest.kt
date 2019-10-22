package be.hogent.faith.faith.emotionCapture.enterText

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
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
class EnterTextFragmentTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToWriteTextScreen()
    }

    @Test
    fun enterTextFragment_canClickAllButtons() {
        onView(withId(R.id.btn_enter_text_setBold)).perform(click())
        onView(withId(R.id.btn_enter_text_setItalic)).perform(click())
        onView(withId(R.id.btn_enter_text_setUnderline)).perform(click())

        onView(withId(R.id.btn_enter_text_setColorBlack)).perform(click())
        onView(withId(R.id.btn_enter_text_setColorBlue)).perform(click())
        onView(withId(R.id.btn_enter_text_setColorYellow)).perform(click())
        onView(withId(R.id.btn_enter_text_setColorGreen)).perform(click())
        onView(withId(R.id.btn_enter_text_setColorRed)).perform(click())

        onView(withId(R.id.btn_enter_text_setFontLarge)).perform(click())
        onView(withId(R.id.btn_enter_text_setFontNormal)).perform(click())
        onView(withId(R.id.btn_enter_text_setFontSmall)).perform(click())
    }

    @Test
    fun enterTextFragment_saveSuccessful_backToPark() {
        closeSoftKeyboard()
        onView(withId(R.id.btn_enter_text_save)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.screen_new_event)).check(matches(isDisplayed()))
    }

    /**
     * Ingeven van een waarde werkt niet daar element met id editor een div tag is. Enige manier is met javascriptcode maar dan test ik geen UI meer, maar de werking van de component
     * Als je onderstaand zou toevoegen dan dependency  espressoWeb:  'androidx.test.espresso:espresso-web:3.1.0' toevoegen
    @Test
    fun enterTextFragment_enterText_OK() {

    onWebView(ViewMatchers.withId(R.id.editor)).forceJavascriptEnabled()
    nWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).perform(webClick())
    onWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).perform(webKeys(stringToBetyped))
    onWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).check(webMatches(getText(), equalTo(stringToBetyped)))
    Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_save)).perform(ViewActions.click())
    ToastMatcher.isToastMessageDisplayed(R.string.frag_entertext_save_successfull)
    }
     */
}
