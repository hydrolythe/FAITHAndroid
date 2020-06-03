package be.hogent.faith.faith.loginOrRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.cityScreen.CityScreenActivity
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarFragment
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarViewModel
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import timber.log.Timber

class LoginOrRegisterActivity : AppCompatActivity(),
    WelcomeFragment.WelcomeNavigationListener,
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    private lateinit var userViewModel: UserViewModel
    private val registerAvatarViewModel: RegisterAvatarViewModel by viewModel<RegisterAvatarViewModel>()
    private val welcomeViewModel: WelcomeViewModel by viewModel<WelcomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Must happen before creating the RegisterFragment because it uses the scope
        createScopedUserViewModel()
        registerListeners()

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

    private fun registerListeners() {
        // once registered or logged in, get the user object...
        userViewModel.getLoggedInUserState.observe(this, Observer {
            it?.let {
                handleDataStateGetLoggedInUser(it)
            }
        })
    }

    private fun handleDataStateGetLoggedInUser(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                userViewModel.user.observe(this, Observer {
                    if (it.avatarName == "null") {
                        goToRegisterAvatarScreen()
                    } else {
                        goToCityScreen()
                    }
                })
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                Toast.makeText(this, resource.message!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createScopedUserViewModel() {
        Timber.i("Creating USER SCOPE")
        // Don't create SCOPE twice
        // This sometimes happens when running tests that reuse the same Activity twice
        var scope: Scope
        try {
            scope = getKoin().createScope(KoinModules.USER_SCOPE_ID, named(USER_SCOPE_NAME))
        } catch (e: ScopeAlreadyCreatedException) {
            scope = getKoin().getScope(KoinModules.USER_SCOPE_ID)
            Timber.i("User scope already existed, not recreating")
        }
        userViewModel = scope.get()
    }

    private fun goToCityScreen() {
        val intent = Intent(this, CityScreenActivity::class.java)
        startActivity(intent)
    }

    override fun userIsRegistered() {
        userViewModel.getLoggedInUser()
    }

    override fun initialiseUser() {
        registerAvatarViewModel.initialiseUser(userViewModel.user.value!!)
    }

    override fun userIsLoggedIn() {
        userViewModel.getLoggedInUser()
    }

    private fun goToRegisterAvatarScreen() {
        replaceFragment(RegisterAvatarFragment.newInstance(), R.id.fragment_container)
    }
}
