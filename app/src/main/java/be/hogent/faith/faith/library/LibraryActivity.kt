package be.hogent.faith.faith.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventDetails.EventDetailFragment
import be.hogent.faith.faith.library.eventList.EventListFragment
import be.hogent.faith.faith.library.eventList.EventListViewModel
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

/**
 * An activity representing a list of Events of the user. This activity
 * has different presentations for small and bigger devices. On
 * smaller devices, the activity presents a list of events, which when touched,
 * lead to a another screen representing
 * event details. On bigger devices, the activity presents the list of events and
 * events details side-by-side using two vertical panes.
 */
class LibraryActivity : AppCompatActivity(), EventListFragment.EventsListNavigationListener {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private val eventListViewModel: EventListViewModel by viewModel { parametersOf(userViewModel.user.value) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        replaceFragment(EventDetailFragment.newInstance(eventUuid), R.id.fragment)
    }
}
