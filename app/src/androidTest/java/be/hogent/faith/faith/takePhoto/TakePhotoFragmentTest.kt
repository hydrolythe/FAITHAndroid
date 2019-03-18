package be.hogent.faith.faith.takePhoto

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.faith.fragmentTestModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.stopKoin

@RunWith(AndroidJUnit4::class)
class TakePhotoFragmentTest {

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(fragmentTestModule)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun takePhotoFragment_launches() {
        launchFragmentInContainer<TakePhotoFragment>()
    }
}