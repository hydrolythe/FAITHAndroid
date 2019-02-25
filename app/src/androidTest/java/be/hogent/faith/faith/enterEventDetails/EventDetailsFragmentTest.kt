package be.hogent.faith.faith.enterEventDetails

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDetailsFragmentTest {

    @Test
    fun eventDetailsFragment_launches() {
        launchFragmentInContainer<EventDetailsFragment>()
    }

//    @Test
//    fun eventDetailSFragment_buttonsCanBeClicked() {
//        launchFragmentInContainer<EventDetailsFragment>()
//
//        onView(withId(R.id.btn_event_details_audio)).perform(click())
//        onView(withId(R.id.btn_event_details_camera)).perform(click())
//        onView(withId(R.id.btn_event_details_drawing)).perform(click())
//        onView(withId(R.id.btn_event_details_text)).perform(click())
//
//        onView(withId(R.id.btn_event_details_gotoEmotionAvatar)).perform(click())
//        onView(withId(R.id.btn_event_details_send)).perform(click())
//    }
}