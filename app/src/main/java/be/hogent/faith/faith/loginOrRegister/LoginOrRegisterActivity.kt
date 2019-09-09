package be.hogent.faith.faith.loginOrRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.cityScreen.CityScreenActivity
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarFragment
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoFragment
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoViewModel
import be.hogent.faith.faith.util.replaceFragment
import be.hogent.faith.util.TAG
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class LoginOrRegisterActivity : AppCompatActivity(),
    WelcomeFragment.WelcomeNavigationListener,
    RegisterUserInfoFragment.RegisterUserInfoNavigationListener,
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    private lateinit var registerUserInfoViewModel: RegisterUserInfoViewModel

    private val loginManager: LoginManager by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerUserInfoViewModel = getViewModel()

        // Must happen before creating the RegisterFragment because it uses the scope
        createScopedUserViewModel()

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = WelcomeFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra(LoginManager.KEY_CLEAR_CREDENTIALS, false)) {
            loginManager.logout()
            getKoin().getScope(KoinModules.USER_SCOPE_ID).close()
        }
    }

    private fun createScopedUserViewModel() {
        Log.e(TAG, "Creating USER SCOPE")
        // Don't create SCOPE twice
        // This sometimes happens when running tests that reuse the same Activity twice
        try {
            getKoin().createScope(KoinModules.USER_SCOPE_ID, named(USER_SCOPE_NAME))
        } catch (e: ScopeNotCreatedException) {
            Log.i(TAG, "User scope already existed, not recreating")
        }
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

    override fun goToRegistrationScreen() {
        replaceFragment(RegisterUserInfoFragment.newInstance(), R.id.fragment_container)
    }

    override fun goToRegisterAvatarScreen() {
        replaceFragment(RegisterAvatarFragment.newInstance(), R.id.fragment_container)
    }
}
