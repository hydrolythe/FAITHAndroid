package be.hogent.faith.faith.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.emotionCapture.ui.EmotionCaptureMainActivity
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),
    MainScreenFragment.MainScreenNavigationListener {

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
