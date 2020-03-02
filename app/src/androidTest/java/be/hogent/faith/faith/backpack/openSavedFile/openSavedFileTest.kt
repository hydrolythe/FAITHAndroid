package be.hogent.faith.faith.backpack.openSavedFile

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
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
import org.koin.test.KoinTest

/*@RunWith(AndroidJUnit4::class)
class BackpackScreenFragmentTest : KoinTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)


    @Before
    fun goToScreen() {
        NavigationUtil.goToBackpack()

    }*/


   /* @Test
    fun backpackFragment_canOpenAudioFile() {
        onView(withId(R.id.recyclerview_backpack)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(1000)
        onView(withId(R.id.btn_recordAudio_play)).check(ViewAssertions.matches(isDisplayed()))
    }
    @Test
    fun backpackFragment_canOpenDrawFile() {
        onView(withId(R.id.recyclerview_backpack)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        Thread.sleep(1000)
        onView(withId(R.id.screen_draw)).check(ViewAssertions.matches(isDisplayed()))
    }
    @Test
    fun backpackFragment_canOpenPhotoFile() {
        onView(withId(R.id.recyclerview_backpack)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))
        Thread.sleep(1000)
        onView(withId(R.id.screen_take_photo)).check(ViewAssertions.matches(isDisplayed()))
    }
    @Test
    fun backpackFragment_canOpenTextFile() {
        onView(withId(R.id.recyclerview_backpack)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        Thread.sleep(1000)
        onView(withId(R.id.enterText_editor)).check(ViewAssertions.matches(isDisplayed()))
    }*/

//}