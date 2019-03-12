package be.hogent.faith.faith.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarFragment
import be.hogent.faith.faith.chooseAvatar.fragments.UserViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.mainScreen.MainScreenFragment
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.faith.util.getViewModel
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),
    EventDetailsFragment.EventDetailsNavigationListener,
    MainScreenFragment.MainScreenNavigationListener {

    private  val _userViewModel : UserViewModel by viewModel()

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

    /**
     * Sets the [User] who is working with the application.
     */
    fun setUser(user : User){
        _userViewModel.setUser(user)
        Log.i(TAG,"Set the user which username ${user.username}")
    }


    override fun startDrawFragment() {
        replaceFragment(DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.fragment_container)
    }

    override fun startEventDetailsFragment() {
        replaceFragment(EventDetailsFragment.newInstance(), R.id.fragment_container)
    }
}