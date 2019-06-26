package be.hogent.faith.faith.loginOrRegister

import android.app.Activity
import android.app.Dialog
import android.widget.Toast
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import org.koin.android.ext.android.inject

class LoginManager {

    /**
     * Authentication variables
     */
    private val auth0: Auth0 by inject()

    private val  credentialsManager : SecureCredentialsManager by inject()

    init {
        auth0.isOIDCConformant = true
    }


    fun login() {
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
}