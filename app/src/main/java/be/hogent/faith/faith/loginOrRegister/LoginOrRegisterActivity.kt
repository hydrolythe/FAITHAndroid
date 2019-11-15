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
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoFragment
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named
import timber.log.Timber

class LoginOrRegisterActivity : AppCompatActivity(),
    WelcomeFragment.WelcomeNavigationListener,
    RegisterUserInfoFragment.RegisterUserInfoNavigationListener,
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    private lateinit var userViewModel: UserViewModel
    private val registerUserViewModel: RegisterUserViewModel by viewModel<RegisterUserViewModel>()
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
        //user is registering, on success get the corresponding user object from the database
        //other states : loading and error are handled in the WelcomeFragment
        registerUserViewModel.userRegisteredState.observe(this, Observer {
            it?.let{
                if (it.status == ResourceState.SUCCESS)
                    onLoginSuccess()
            }
        })

        //user is logging in, on success get the corresponding user object from the database
        //other states : loading and error are handled in the WelcomeFragment
        welcomeViewModel.userLoggedInState.observe(this, Observer {
            it?.let{
                if (it.status == ResourceState.SUCCESS)
                    onLoginSuccess()
            }
        })

        //once registered or logged in, get the user object...
        userViewModel.getLoggedInUserState.observe(this, Observer {
            it?.let {
                handleDataStateGetLoggedInUser(it)
            }
        })
    }

    private fun onLoginSuccess() {
        userViewModel.getLoggedInUser()
    }

    private fun handleDataStateGetLoggedInUser(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                goToCityScreen()
            }
            ResourceState.LOADING -> {

            }
            ResourceState.ERROR -> {
                Toast.makeText(this, resource.message!!, Toast.LENGTH_LONG).show()}
        }
    }

    private fun createScopedUserViewModel() {
        Timber.i("Creating USER SCOPE")
        // Don't create SCOPE twice
        // This sometimes happens when running tests that reuse the same Activity twice
        try {
            val scope = getKoin().createScope(KoinModules.USER_SCOPE_ID, named(USER_SCOPE_NAME))
            userViewModel = scope.get()

        } catch (e: ScopeAlreadyCreatedException) {
            getKoin().getScope(KoinModules.USER_SCOPE_ID)
            Timber.i("User scope already existed, not recreating")
        }
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
