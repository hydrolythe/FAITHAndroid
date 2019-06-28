package be.hogent.faith.faith.loginOrRegister

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import be.hogent.faith.util.TAG
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import org.koin.android.ext.android.inject

import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_welcome.background_welcome

import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class WelcomeFragment : Fragment(), LoginManager.LoginCallback {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by viewModel<WelcomeViewModel>()

    private val loginManager: LoginManager by inject { parametersOf( this)}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentWelcomeBinding =
            DataBindingUtil.inflate(inflater, be.hogent.faith.R.layout.fragment_welcome, container, false)
        binding.welcomeViewModel = welcomeViewModel
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        registerListeners()
        //  setBackgroundImage()
    }

    private fun setBackgroundImage() {
        Glide.with(requireContext())
            .load(R.drawable.loginscherm)
            .into(background_welcome)
    }

    private fun registerListeners() {
        welcomeViewModel.registerButtonClicked.observe(this, Observer {
            navigation!!.goToRegistrationScreen()
        })
        welcomeViewModel.loginButtonClicked.observe(this, Observer {
            loginManager.login(activity!!)
        })
        welcomeViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WelcomeNavigationListener) {
            navigation = context
        }
    }

    interface WelcomeNavigationListener {
        fun goToRegistrationScreen()
        fun goToCityScreen()
    }


    override fun onSuccess() {
        if (getKoin().scopeRegistry.getScope(KoinModules.USER_SCOPE_ID) == null) {
            getKoin().createScope(KoinModules.USER_SCOPE_ID)
        }
        val userViewModel: UserViewModel = get(scope = getKoin().getScope(KoinModules.USER_SCOPE_ID))
        // TODO: Call the Auth0 profile and retrieve user name
        userViewModel.setUser(User("testuser", avatarName = "meisje_stoer"))
        navigation!!.goToCityScreen()
    }

    override fun onFailure(msg: String) {
        Toast.makeText(activity, "Log In - Error Occurred : $msg", Toast.LENGTH_SHORT).show()
    }


    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}
