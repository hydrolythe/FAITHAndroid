package be.hogent.faith.faith.overviewEvents

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewEventsFragmentTest {

        @Test
        fun overviewEventsFragment_launches() {
            launchFragmentInContainer<OverviewEventsFragment>()
        }
    }
