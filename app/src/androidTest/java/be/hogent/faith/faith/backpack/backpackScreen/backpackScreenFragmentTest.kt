package be.hogent.faith.faith.backpack.backpackScreen
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.util.NavigationUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
@LargeTest
class BackpackScreenFragmentTest : KoinTest {

    @get:Rule
    var activityScenarioRule =
        ActivityTestRule<LoginOrRegisterActivity>(LoginOrRegisterActivity::class.java, true, true)

    @Before
    fun goToScreen() {
        NavigationUtil.goToBackpack()

    }

    @Test
    fun backpackFragment_canClickAllButtons() {
        onView(withId(R.id.btn_backpack_draw_cancel)).perform(click())
        onView(withId(R.id.btn_backpack_search)).perform(click())
        onView(withId(R.id.btn_backpack_add)).perform(click())
        onView(withId(R.id.btn_backpack_delete)).perform(click())

    }
}
