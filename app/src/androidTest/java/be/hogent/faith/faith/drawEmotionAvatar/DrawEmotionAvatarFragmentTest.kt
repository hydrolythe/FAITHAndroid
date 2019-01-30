package be.hogent.faith.faith.drawEmotionAvatar

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawEmotionAvatarFragmentTest {
    @Test
    fun drawEmotionFragment_launches() {
        launchFragmentInContainer<DrawEmotionAvatarFragment>()
    }
}