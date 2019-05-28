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
import org.koin.android.viewmodel.ext.android.viewModel


class WelcomeFragment : Fragment() {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by viewModel<WelcomeViewModel>()



    /**
     * Authentication variables
     */
    private val auth0: Auth0 by inject()
    private val client : AuthenticationAPIClient by inject()
    private val  credentialsManager : SecureCredentialsManager by inject()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentWelcomeBinding =
            DataBindingUtil.inflate(inflater, be.hogent.faith.R.layout.fragment_welcome, container, false)
        binding.welcomeViewModel = welcomeViewModel
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        auth0.isOIDCConformant = true
        registerListeners()

        // Obtain the existing credentials and move to the next activity
        credentialsManager.getCredentials(object : BaseCallback<Credentials, CredentialsManagerException> {
           override fun onSuccess(credentials: Credentials) {
                navigation!!.goToCityScreen()
            }

            override fun onFailure(error: CredentialsManagerException) {
                //Authentication cancelled by the user. Exit the app
                Log.e(TAG,"${error.message}")
            }
        })

    }

    private fun registerListeners() {
        welcomeViewModel.registerButtonClicked.observe(this, Observer {
            navigation!!.goToRegistrationScreen()
        })
        welcomeViewModel.loginButtonClicked.observe(this, Observer {
            login()
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

    private fun login() {
        WebAuthProvider.init(auth0)
            .withScheme("app")
            //Allow refresh tokens
            .withScope("openid offline_access")
            .withAudience(String.format("https://%s/userinfo", getString(be.hogent.faith.R.string.com_auth0_domain)))
            .start(activity as Activity, webCallback)

    }


    private val webCallback = object : AuthCallback {
        override fun onFailure( dialog: Dialog) {
            activity!!.runOnUiThread(Runnable { dialog.show() })
        }

        override fun onFailure(exception: AuthenticationException) {
            activity!!.runOnUiThread(Runnable {
                Toast.makeText(activity, "Log In - Error Occurred", Toast.LENGTH_SHORT).show()
            })
        }

        override fun onSuccess(credentials: Credentials) {
            activity!!.runOnUiThread(Runnable {
                Toast.makeText(activity, "Log In - Success", Toast.LENGTH_SHORT).show()
            })
            credentialsManager.saveCredentials(credentials)
            navigation!!.goToCityScreen()
        }
    }


    companion object {

        private val CODE_DEVICE_AUTHENTICATION = 22



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
