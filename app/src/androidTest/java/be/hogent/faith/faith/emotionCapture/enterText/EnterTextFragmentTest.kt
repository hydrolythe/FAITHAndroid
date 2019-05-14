package be.hogent.faith.faith.emotionCapture.enterText

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import be.hogent.faith.R
import be.hogent.faith.faith.registerAvatar.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EnterTextFragmentTest {

    private lateinit var uiDevice: UiDevice

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        NavigationUtil.goToWriteTextScreen()
    }

    @Test()
    fun enterTextFragment_canClickAllButtons() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_save)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setBold)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setItalic)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setUnderline)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setColorBlack)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setColorBlue)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setColorYellow)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setColorGreen)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setColorRed)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setFontLarge)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setFontNormal)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_setFontSmall)).perform(ViewActions.click())
    }

    /**
     * Ingeven van een waarde werkt niet daar element met id editor een div tag is. Enige manier is met javascriptcode maar dan test ik geen UI meer, maar de werking van de component
     * Als je onderstaand zou toevoegen dan dependency  espressoWeb:  'androidx.test.espresso:espresso-web:3.1.0' toevoegen
    @Test
    fun enterTextFragment_enterText_OK() {

    val stringToBetyped = "Hello world"
    onWebView(ViewMatchers.withId(R.id.editor)).forceJavascriptEnabled()
    nWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).perform(webClick())
    onWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).perform(webKeys(stringToBetyped))
    onWebView(ViewMatchers.withId(R.id.editor)).withElement(findElement(Locator.ID, "editor")).check(webMatches(getText(), equalTo(stringToBetyped)))
    Espresso.onView(ViewMatchers.withId(R.id.btn_enter_text_save)).perform(ViewActions.click())
    ToastMatcher.isToastMessageDisplayed(R.string.frag_entertext_save_successfull)
    }
     */
}
