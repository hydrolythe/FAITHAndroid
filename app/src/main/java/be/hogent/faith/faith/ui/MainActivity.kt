package be.hogent.faith.faith.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.editDetail.DetailType
import be.hogent.faith.faith.editDetail.EditDetailFragment
import be.hogent.faith.faith.emotionCapture.ui.EmotionCaptureMainActivity
import be.hogent.faith.faith.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.util.replaceFragment
import com.google.android.material.navigation.NavigationView
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class MainActivity : AppCompatActivity(),
    MainScreenFragment.MainScreenNavigationListener{

    private val userViewModel by viewModel<UserViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            // val fragment = AvatarFragment.newInstance()
            val fragment = MainScreenFragment.newInstance()
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



    override fun startEventDetailsFragment() {
        val intent = Intent(this, EmotionCaptureMainActivity::class.java)
       // intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
       // replaceFragment(EventDetailsFragment.newInstance(), R.id.fragment_container)
    }

    override fun startOverviewEventsFragment() {
        replaceFragment(OverviewEventsFragment.newInstance(), R.id.fragment_container)
    }


}
