package be.hogent.faith.faith.loginOrRegister

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named

class WelcomeFragment : Fragment(), LoginManager.LoginSuccessListener {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by viewModel<WelcomeViewModel>()

    private val loginManager: LoginManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: be.hogent.faith.databinding.FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.welcomeViewModel = welcomeViewModel

        loginManager.attachLoginSuccesListener(this)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
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

    override fun onLoginSuccess() {
        val scope = try {
            getKoin().createScope(KoinModules.USER_SCOPE_ID, named(USER_SCOPE_NAME))
        } catch (e: ScopeAlreadyCreatedException) {
            getKoin().getScope(KoinModules.USER_SCOPE_ID)
        }
        val userViewModel: UserViewModel = scope.get()
        // TODO: Call the Auth0 profile and retrieve user name
        userViewModel.setUser(User("testuser", avatarName = "meisje_stoer"))
        navigation!!.goToCityScreen()
    }

    override fun onLoginFailure(msg: String) {
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
