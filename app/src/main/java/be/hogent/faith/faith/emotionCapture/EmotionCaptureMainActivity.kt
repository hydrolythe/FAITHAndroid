package be.hogent.faith.faith.emotionCapture

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.DetailFinishedListener
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.drawing.create.DrawViewModel
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.emotionCapture.editDetail.DetailFragmentWithEmotionAvatar
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.util.replaceFragment
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_drawer_layout
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_nav_view
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.UUID

class EmotionCaptureMainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    OverviewEventsFragment.OverviewEventsNavigationListener,
    DetailFragmentWithEmotionAvatar.EditDetailNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawingDetailFragment.DrawingScreenNavigation,
    DetailFinishedListener,
    TextDetailFragment.TextScreenNavigation,
    TakePhotoFragment.PhotoScreenNavigation,
    DetailViewHolder.ExistingDetailNavigationListener {

    // This ViewModel is for the [DrawEmotionAvatarFragment], but has been defined here because it should
    // survive the activity's lifecycle, not just its own.
    // Reason: every time [startDrawFragment] is called, a new Fragment is created. In order to retain what has
    // already been painted, the paths are saved in the [DrawEmotionViewModel]. Because we save it here, we can
    // give every new [DrawEmotionAvatarFragment] that same ViewModel, resulting in the drawing being fully restored.
    // Saving the fragment as a property doesn't work because the property doesn't survive configuration changes.
    // Saving the fragment somewhere in the backstack might work, but would require complicated backstack management.
    private val drawEmotionViewModel by viewModel<DrawViewModel>()

    // This VM is made here because it holds the event that is described in the EventDetailsFragment and
    // the fragments that can be started from there.
    // They all require the same event object so it has to be shared.
    private lateinit var eventViewModel: EventViewModel

    private lateinit var takePhotoViewModel: TakePhotoViewModel

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val avatarProvider: AvatarProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_capture)

        eventViewModel = getViewModel()
        takePhotoViewModel = getViewModel()

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
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
                    showExitAlert()
                }
            }
            true
        }
    }

    private fun showExitAlert() {
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

    private fun closeEventSpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    override fun startDrawEmotionAvatarFragment() {
        val avatar =
            avatarProvider.getAvatarDrawableOutlineId(userViewModel.user.value!!.avatarName)
        replaceFragment(
            DrawEmotionAvatarFragment.newInstance(avatar), R.id.emotionCapture_fragment_container
        )
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        replaceFragment(
            EventDetailsFragment.newInstance(eventUuid),
            R.id.emotionCapture_fragment_container
        )
    }

    override fun openDetailScreenFor(detail: Detail) {
        val avatar = getAvatarOutline()
        replaceFragment(
            DetailFragmentWithEmotionAvatar.newInstance(detail, avatar),
            R.id.emotionCapture_fragment_container
        )
    }

    private fun getAvatarOutline() =
        avatarProvider.getAvatarDrawableOutlineId(userViewModel.user.value!!.avatarName)

    override fun startPhotoDetailFragment() {
        replaceFragment(
            DetailFragmentWithEmotionAvatar.PhotoFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            ), R.id.emotionCapture_fragment_container
        )
    }

    override fun startAudioDetailFragment() {
        replaceFragment(
            DetailFragmentWithEmotionAvatar.AudioFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            ), R.id.emotionCapture_fragment_container
        )
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(
            DetailFragmentWithEmotionAvatar.DrawingFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            ), R.id.emotionCapture_fragment_container
        )
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
        }
    }

    private fun save(drawingDetail: DrawingDetail) {
        eventViewModel.saveDrawingDetail(drawingDetail)
        Toast.makeText(this, R.string.save_drawing_success, Toast.LENGTH_SHORT).show()
    }

    private fun save(textDetail: TextDetail) {
        eventViewModel.saveTextDetail(textDetail)
        Toast.makeText(this, R.string.save_text_success, Toast.LENGTH_SHORT).show()
    }

    override fun startTextDetailFragment() {
        replaceFragment(
            DetailFragmentWithEmotionAvatar.TextFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            ), R.id.emotionCapture_fragment_container
        )
    }

    override fun backToEvent() {
        supportFragmentManager.popBackStack()
    }

    override fun closeEvent() {
        closeEventSpecificScopes()
        finish()
    }

    override fun onBackPressed() {
        val f =
            supportFragmentManager.findFragmentById(R.id.emotionCapture_fragment_container)
        if (f is EventDetailsFragment) {
            showExitAlert()
        } else {
            super.onBackPressed()
        }
    }
}