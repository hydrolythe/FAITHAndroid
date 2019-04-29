package be.hogent.faith.faith.emotionCapture

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.DetailType
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.registerAvatar.UserViewModel
import be.hogent.faith.faith.util.replaceFragment
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_drawer_layout
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_nav_view
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class EmotionCaptureMainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    OverviewEventsFragment.OverviewEventsNavigationListener,
    EditDetailFragment.EditDetailNavigationListener {

    // This ViewModel is for the [DrawEmotionAvatarFragment], but has been defined here because it should
    // survive the activity's lifecycle, not just its own.
    // Reason: every time [startDrawFragment] is called, a new Fragment is created. In order to retain what has
    // already been painted, the paths are saved in the [DrawEmotionViewModel]. Because we save it here, we can
    // give every new [DrawEmotionAvatarFragment] that same ViewModel, resulting in the drawing being fully restored.
    // Saving the fragment as a property doesn't work because the property doesn't survive configuration changes.
    // Saving the fragment somewhere in the backstack might work, but would require complicated backstack management.
    private val drawEmotionViewModel by viewModel<DrawEmotionViewModel>()

    // This VM is made here because it holds the event that is described in the EventDetailsFragment and
    // the fragments that can be started from there.
    // They all require the same event object so it has to be shared.
    // This may cause issues when entering multiple events. A possible solution might be to have an Activity for each
    // of the 4 main functions of the app.
    private lateinit var eventViewModel: EventViewModel

    private lateinit var takePhotoViewModel: TakePhotoViewModel

    private val userViewModel by inject<UserViewModel>()

    lateinit var recordAudioViewModel: RecordAudioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_capture)

        eventViewModel = getViewModel {
            parametersOf(userViewModel.user)
        }

        takePhotoViewModel = getViewModel {
            parametersOf(eventViewModel.event.value)
        }
        recordAudioViewModel = getViewModel {
            parametersOf(eventViewModel.event.value)
        }

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            // val fragment = RegisterAvatarFragment.newInstance()
            val fragment = EventDetailsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.emotionCapture_fragment_container, fragment)
                .commit()
        }

        emotionCapture_nav_view.setNavigationItemSelectedListener { menuItem ->
            // close drawer when item is tapped
            emotionCapture_drawer_layout.closeDrawers()
            // perform action
            when (menuItem.itemId) {
                R.id.nav_city -> {
                    val alertDialog: AlertDialog = this.run {
                        val builder = AlertDialog.Builder(this).apply {
                            setTitle(R.string.dialog_to_the_city_title)
                            setMessage(R.string.dialog_to_the_city_message)
                            setPositiveButton(R.string.ok) { _, _ ->
                                finish()
                            }
                            setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                        }
                        builder.create()
                    }
                    alertDialog.show()
                }
            }
            true
        }
    }

    override fun startDrawEmotionAvatarFragment() {
        replaceFragment(
            DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.emotionCapture_fragment_container
        )
    }

    override fun startTakePhotoFragment() {
        replaceFragment(TakePhotoFragment.newInstance(), R.id.emotionCapture_fragment_container)
    }

    override fun startRecordAudioFragment() {
        replaceFragment(RecordAudioFragment.newInstance(), R.id.emotionCapture_fragment_container)
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        replaceFragment(EventDetailsFragment.newInstance(eventUuid), R.id.emotionCapture_fragment_container)
    }

    override fun startEventDetail(type: DetailType) {
        replaceFragment(
            EditDetailFragment.newInstance(type, R.drawable.outline),
            R.id.emotionCapture_fragment_container
        )
    }
}