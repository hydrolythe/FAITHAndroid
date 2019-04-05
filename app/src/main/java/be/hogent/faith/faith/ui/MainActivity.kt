package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.makeDrawing.DrawViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.makeDrawing.MakeDrawingFragment
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class MainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    OverviewEventsFragment.OverviewEventsNavigationListener,
    MainScreenFragment.MainScreenNavigationListener,
    TakePhotoFragment.TakePhotoNavigationListener {

    // This ViewModel is for the [DrawEmotionAvatarFragment], but has been defined here because it should
    // survive the activity's lifecycle, not just its own.
    // Reason: every time [startDrawFragment] is called, a new Fragment is created. In order to retain what has
    // already been painted, the paths are saved in the [DrawViewModel]. Because we save it here, we can
    // give every new [DrawEmotionAvatarFragment] that same ViewModel, resulting in the drawing being fully restored.
    // Saving the fragment as a property doesn't work because the property doesn't survive configuration changes.
    // Saving the fragment somewhere in the backstack might work, but would require complicated backstack management.
    private val drawEmotionViewModel by viewModel<DrawViewModel>()

    // This VM is made here because it holds the event that is described in the EventDetailsFragment and
    // the fragments that can be started from there.
    // They all require the same event object so it has to be shared.
    // This may cause issues when entering multiple events. A possible solution might be to have an Activity for each
    // of the 4 main functions of the app.
    lateinit var eventDetailsViewModel: EventDetailsViewModel

    lateinit var takePhotoViewModel: TakePhotoViewModel
    private val userViewModel by viewModel<UserViewModel>()

    lateinit var recordAudioViewModel: RecordAudioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eventDetailsViewModel = getViewModel {
            parametersOf(userViewModel.user)
        }
        takePhotoViewModel = getViewModel {
            parametersOf(eventDetailsViewModel.event.value)
        }
        recordAudioViewModel = getViewModel {
            parametersOf(eventDetailsViewModel.event.value)
        }

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
//            val fragment = MainScreenFragment()
            val fragment = MakeDrawingFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

//    /**
//     * Sets the [User] currently working with the application.
//     */
//    fun setUser(user: User) {
//        _userViewModel.setUser(user)
//        Log.i(TAG, "Set the user which username ${user.username}")
//    }

    override fun startDrawEmotionAvatarFragment() {
        replaceFragment(DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.fragment_container)
    }

    override fun startTakePhotoFragment() {
        replaceFragment(TakePhotoFragment.newInstance(), R.id.fragment_container)
    }

    override fun startEventDetailsFragment() {
        replaceFragment(EventDetailsFragment.newInstance(), R.id.fragment_container)
    }

    override fun startOverviewEventsFragment() {
        replaceFragment(OverviewEventsFragment.newInstance(), R.id.fragment_container)
    }

    override fun startRecordAudioFragment() {
        replaceFragment(RecordAudioFragment.newInstance(), R.id.fragment_container)
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        replaceFragment(EventDetailsFragment.newInstance(eventUuid), R.id.fragment_container)
    }
}