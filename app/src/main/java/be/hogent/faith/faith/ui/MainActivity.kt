package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarFragment
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.util.replaceFragment

class MainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    MainScreenFragment.MainScreenNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = AvatarFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun startDrawFragment() {
        replaceFragment(DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.fragment_container)
    }

    override fun startEventDetailsFragment() {
        replaceFragment(EventDetailsFragment.newInstance(), R.id.fragment_container)
    }
}