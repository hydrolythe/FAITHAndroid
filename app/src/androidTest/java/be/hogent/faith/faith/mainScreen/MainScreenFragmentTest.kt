package be.hogent.faith.faith.mainScreen

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenFragmentTest {
    @Test
    fun drawEmotionFragment_launches() {
        launchFragmentInContainer<MainScreenFragment>()
    }
}