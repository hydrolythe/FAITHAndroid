package be.hogent.faith.faith.backpackScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.editDetail.DetailFragmentWithEmotionAvatar
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.UUID

class BackpackScreenActivity : AppCompatActivity(), BackpackScreenFragment.BackpackDetailsNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
    DetailFinishedListener,
    TextDetailFragment.TextScreenNavigation,
    TakePhotoFragment.PhotoScreenNavigation,
    DetailViewHolder.ExistingDetailNavigationListener {

    private lateinit var backpackViewModel: BackpackViewModel

   // private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backpackscreen)

        backpackViewModel = getViewModel()

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = BackpackScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, fragment)
                .commit()
        }
    }

    override fun backToEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDetailFinished(detail: Detail) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(BackpackDetailFragment.PhotoFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
    }

    override fun startAudioDetailFragment() {
        replaceFragment(BackpackDetailFragment.AudioFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(BackpackDetailFragment.DrawingFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
    }

    override fun startTextDetailFragment() {
        replaceFragment(
            BackpackDetailFragment.TextFragmentNoEmotionAvatar.newInstance(), R.id.fragment
        )
    }

    override fun startVideoDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startFileDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openDetailScreenFor(detail: Detail) {
        replaceFragment(
            BackpackDetailFragment.newInstance(detail),
            R.id.fragment
        )    }

    override fun closeEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}