package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.takePicture.TakePhotoFragment
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    MainScreenFragment.MainScreenNavigationListener,
    TakePhotoFragment.TakePhotoNavigationListener {

    // This ViewModel is for the [DrawEmotionAvatarFragment], but has been defined here because it should
    // survive the activitiy's lifecycle, not just its own.
    // Reason: every time [startDrawFragment] is called, a new Fragment is created. In order to retain what has
    // already been painted, the paths are saved in the [DrawEmotionViewModel]. Because we save it here, we can
    // give every new [DrawEmotionAvatarFragment] that same ViewModel, resulting in the drawing being fully restored.
    // Saving the fragment as a property doesn't work because the property doesn't survive configuration changes.
    // Saving the fragment somewhere in the backstack might work, but would require complicated backstack management.
    private val DrawEmotionViewModel by viewModel<DrawEmotionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
//            val fragment = TakePhotoFragment()
            val fragment = MainScreenFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun startDrawEmotionAvatarFragment() {
        replaceFragment(DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.fragment_container)
    }

    override fun startEventDetailsFragment() {
        replaceFragment(EventDetailsFragment.newInstance(), R.id.fragment_container)
    }
}