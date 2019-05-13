package be.hogent.faith.faith.registerAvatar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.cityScreen.CityScreenActivity
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.util.TAG
import org.koin.android.ext.android.getKoin

class LoginOrRegisterActivity : AppCompatActivity(),
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Must happen before creating the RegisterFragment because it uses the scope
        createScopedUserViewModel()

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

    private fun createScopedUserViewModel() {
        Log.e(TAG, "Creating USER SCOPE")
        getKoin().createScope(KoinModules.USER_SCOPE_ID)
    }

    override fun onDestroy() {
        Log.e(TAG, "Destroying USER SCOPE")
        getKoin().getScope(KoinModules.USER_SCOPE_ID).close()
        super.onDestroy()
    }

    override fun goToCityScreen() {
        val intent = Intent(this, CityScreenActivity::class.java)
        startActivity(intent)
    }
}
