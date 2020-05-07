package be.hogent.faith.faith.emotionCapture

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.drawing.create.DrawViewModel
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.emotionCapture.editDetail.DetailFragmentWithEmotionAvatar
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.util.replaceFragment
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_drawer_layout
import kotlinx.android.synthetic.main.activity_emotion_capture.emotionCapture_nav_view
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.UUID

class EmotionCaptureMainActivity : AppCompatActivity(),
    EventFragment.EventDetailsNavigationListener,
    DetailFragmentWithEmotionAvatar.EditDetailNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
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

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val avatarProvider: AvatarProvider by inject()
    private var alertDialog: AlertDialog? = null
    private lateinit var emotionAvatarFragment: DrawEmotionAvatarFragment

    // tag for detailfragment, so it can be hidden and shown again
    private var tagDetail: String? = null

    // tag for avatarfragment, so it can be hidden and shown again
    private var tagEmotionAvatar: String? = null

    // if we have to go back to the detail iso the park when saving or canceling the avatarfragment
    private var toAvatarFromDetail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_capture)

        eventViewModel = getViewModel()

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = EventFragment.newInstance()
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

        val avatar =
            avatarProvider.getAvatarDrawableOutlineId(userViewModel.user.value!!.avatarName)
        emotionAvatarFragment = DrawEmotionAvatarFragment.newInstance(avatar)
        supportFragmentManager.beginTransaction()
            .add(R.id.emotionCapture_fragment_container, emotionAvatarFragment, "avatar")
            .hide(emotionAvatarFragment).commit()
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.audioDetailSavedSuccessFully.observe(this, Observer { messageResId ->
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.drawingDetailSavedSuccessFully.observe(this, Observer { messageResId ->
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.textDetailSavedSuccessFully.observe(this, Observer { messageResId ->
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.photoDetailSavedSuccessFully.observe(this, Observer { messageResId ->
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessageResId ->
            Toast.makeText(this, errorMessageResId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.cancelButtonClicked.observe(this, Observer {
            onBackPressed()
        })
    }

    private fun showExitAlert() {
        alertDialog = this.run {
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
        alertDialog!!.show()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.cancel()
        }
    }

    private fun closeEventSpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    override fun startDrawEmotionAvatarFragment() {
        // the detailfragment together with the avatar
        if (tagDetail != null && tagEmotionAvatar != null) {
            supportFragmentManager.beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(tagDetail)!!)
                .show(supportFragmentManager.findFragmentByTag(tagEmotionAvatar)!!).commit()
            toAvatarFromDetail = true
        } else {
            // only the avatarfragment
            val avatar =
                avatarProvider.getAvatarDrawableOutlineId(userViewModel.user.value!!.avatarName)
            replaceFragment(
                DrawEmotionAvatarFragment.newInstance(avatar),
                R.id.emotionCapture_fragment_container
            )
            toAvatarFromDetail = false
        }
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
        startDetail(
            DetailFragmentWithEmotionAvatar.PhotoFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            )
        )
    }

    override fun startAudioDetailFragment() {
        startDetail(
            DetailFragmentWithEmotionAvatar.AudioFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            )
        )
    }

    override fun startDrawingDetailFragment() {
        startDetail(
            DetailFragmentWithEmotionAvatar.DrawingFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            )
        )
    }

    private fun startDetail(fragmentDetail: DetailFragmentWithEmotionAvatar) {
        tagDetail = UUID.randomUUID().toString() // create a unique tagname for the detailFragment
        tagEmotionAvatar =
            UUID.randomUUID().toString() // create a unique tagname for the avatarFragment

        val avatar =
            avatarProvider.getAvatarDrawableOutlineId(userViewModel.user.value!!.avatarName)
        val fragmentAvatar = DrawEmotionAvatarFragment.newInstance(avatar)
        replaceFragment(fragmentDetail, R.id.emotionCapture_fragment_container, tagDetail)
        // hide the avatarFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.emotionCapture_fragment_container, fragmentAvatar, tagEmotionAvatar)
            .hide(fragmentAvatar).commit()
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is AudioDetail -> save(detail)
        }
    }

    private fun save(drawingDetail: DrawingDetail) {
        eventViewModel.saveDrawingDetail(drawingDetail)
    }

    private fun save(textDetail: TextDetail) {
        eventViewModel.saveTextDetail(textDetail)
    }

    private fun save(photoDetail: PhotoDetail) {
        eventViewModel.savePhotoDetail(photoDetail)
    }

    private fun save(audioDetail: AudioDetail) {
        eventViewModel.saveAudioDetail(audioDetail)
    }

    override fun deleteDetail(detail: Detail) {
        eventViewModel.deleteDetail(detail)
    }

    override fun startTextDetailFragment() {
        startDetail(
            DetailFragmentWithEmotionAvatar.TextFragmentWithEmotionAvatar.newInstance(
                getAvatarOutline()
            )
        )
    }

    override fun backToEvent() {
        if (toAvatarFromDetail) {
            supportFragmentManager.beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(tagEmotionAvatar)!!)
                .show(supportFragmentManager.findFragmentByTag(tagDetail)!!)
                .commit()
            toAvatarFromDetail = false
        } else {
            tagDetail = null
            tagEmotionAvatar = null
            toAvatarFromDetail = false
            supportFragmentManager.popBackStack()
        }
    }

    override fun closeEvent() {
        closeEventSpecificScopes()
        finish()
    }

    override fun onBackPressed() {
        val f =
            supportFragmentManager.findFragmentById(R.id.emotionCapture_fragment_container)
        if (f is EventFragment) {
            showExitAlert()
        } else
            super.onBackPressed()
    }
}