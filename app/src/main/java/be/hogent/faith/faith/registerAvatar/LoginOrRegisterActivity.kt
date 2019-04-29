package be.hogent.faith.faith.registerAvatar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.cityScreen.CityScreenActivity
import org.koin.android.viewmodel.ext.android.viewModel

class LoginOrRegisterActivity : AppCompatActivity(),
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    private val userViewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = RegisterAvatarFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun goToCityScreen() {
        val intent = Intent(this, CityScreenActivity::class.java)
        startActivity(intent)
    }
}

