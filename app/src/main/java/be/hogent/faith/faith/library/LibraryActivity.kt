package be.hogent.faith.faith.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.view.ViewDrawingFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.details.text.view.ViewTextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.library.eventDetails.EventDetailFragment
import be.hogent.faith.faith.library.eventDetails.EventDetailsViewModel
import be.hogent.faith.faith.library.eventList.EventListFragment
import be.hogent.faith.faith.library.eventList.EventListViewModel
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

/**
 * An activity representing a list of Events of the user.
 */
class LibraryActivity : AppCompatActivity(), EventListFragment.EventsListNavigationListener,
    DetailViewHolder.ExistingDetailNavigationListener {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private val eventListViewModel: EventListViewModel by viewModel { parametersOf(userViewModel.user.value) }

    private val eventDetailsViewModel: EventDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = EventListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        eventListViewModel.getEvent(eventUuid)?.let {
            eventDetailsViewModel.setEvent(it)
            replaceFragment(EventDetailFragment.newInstance(), R.id.fragment_container)
        }
    }

    override fun openDetailScreenFor(detail: Detail) {
        when (detail) {
            is AudioDetail -> RecordAudioFragment.newInstance(detail)
            is TextDetail -> ViewTextDetailFragment.newInstance(detail)
            is DrawingDetail -> ViewDrawingFragment.newInstance(detail)
            is PhotoDetail -> ReviewPhotoFragment.newInstance(detail)
            is YoutubeVideoDetail -> null
            is ExternalVideoDetail -> null
        }?.let {
            replaceFragment(it, R.id.fragment_container)
        }
    }

    override fun deleteDetail(detail: Detail) {
    }
}