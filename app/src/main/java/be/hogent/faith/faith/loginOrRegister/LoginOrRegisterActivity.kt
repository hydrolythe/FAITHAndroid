package be.hogent.faith.faith.loginOrRegister

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.JWTUtils
import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.RetrofitRequestAdapter
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.cityScreen.CityScreenActivity
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarFragment
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarViewModel
import be.hogent.faith.faith.util.SharedPreferencesHelper
import be.hogent.faith.faith.util.replaceFragment
import be.hogent.faith.faith.util.state.Resource
import be.hogent.faith.faith.util.state.ResourceState
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import timber.log.Timber
import java.util.Date


class LoginOrRegisterActivity : AppCompatActivity(),
    WelcomeFragment.WelcomeNavigationListener,
    RegisterAvatarFragment.AvatarFragmentNavigationListener {

    private lateinit var userViewModel: UserViewModel
    private val registerAvatarViewModel: RegisterAvatarViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val memes = PreferenceManager.getDefaultSharedPreferences(this).edit()
        memes.clear()
        memes.commit()
        RetrofitAdapter.getinstance(this)
        RetrofitRequestAdapter.getinstance(this)
        setContentView(R.layout.activity_main)

        // Must happen before creating the RegisterFragment because it uses the scope
        createScopedUserViewModel()
        registerListeners()

        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null && !sharedPreferences.contains("currentUser")) {
            val fragment = WelcomeFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
        if(sharedPreferences.contains("currentUser")){
            val key = sharedPreferences.getString("currentUser", "")
            val mEditor = sharedPreferences.edit()
            mEditor.remove("currentUser")
            mEditor.apply()
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
        userViewModel.tokenMessage.observe(this, Observer{
            if(it.success != null){
                val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
                val mEditor = sharedPreferences.edit()
                mEditor.putString("currentUser",it.success.token)
                mEditor.apply()
            }
        })
    }

    private fun handleDataStateGetLoggedInUser(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                userViewModel.user.observe(this, Observer {
                    if (it.avatarName == "") {
                        goToRegisterAvatarScreen()
                    } else {
                        SharedPreferencesHelper.setAvatarName(this, it.avatarName)
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
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                userViewModel.getLoggedInUser(it.result?.token)
            }
        }
    }

    override fun initialiseUser() {
        userViewModel.user.value?.let {it ->
            registerAvatarViewModel.initialiseUser(it)
            SharedPreferencesHelper.setAvatarName(this, it.avatarName)
        }
    }

    override fun userIsLoggedIn() {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                userViewModel.getLoggedInUser(it.result?.token)
            }
        }
    }

    private fun goToRegisterAvatarScreen() {
        replaceFragment(RegisterAvatarFragment.newInstance(), R.id.fragment_container)
    }
}
