package be.hogent.faith.faith.chooseAvatar

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.R
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewAvatarTest {

    @Test
    fun newAvatar_launches() {
        launchFragmentInContainer<AvatarFragment>()
        onView(withId(R.id.avatar_txt_name)).perform(typeText("Reinhard"))
    }

    @Test
    fun checkCanEnterName() {
        onView(withId(R.id.avatar_txt_name)).perform(typeText("Reinhard"))
    }
}