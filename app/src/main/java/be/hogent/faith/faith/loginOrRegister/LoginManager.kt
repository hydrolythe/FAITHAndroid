package be.hogent.faith.faith.loginOrRegister

import android.app.Activity
import android.app.Dialog
import be.hogent.faith.R
import be.hogent.faith.faith.di.KoinModules
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber

class LoginManager : KoinComponent {

    private var loginSuccessListener: LoginSuccessListener? = null

    /**
     * Authentication variables
     */
    private val auth0: Auth0 = get()

    private val credentialsManager: SecureCredentialsManager = get()

    init {
        auth0.isOIDCConformant = true
    }

    fun attachLoginSuccesListener(listener: LoginSuccessListener) {
        loginSuccessListener = listener
    }

    fun login(activity: Activity) {
        // Obtain the existing credentials and move to the next activity
        if (credentialsManager.hasValidCredentials()) {
            Timber.i("Still Found credentials")
        }
        credentialsManager.getCredentials(object :
            BaseCallback<Credentials, CredentialsManagerException> {

            /**
             * User was already logged in
             */
            override fun onSuccess(credentials: Credentials) {
                loginSuccessListener?.onLoginSuccess()
            }

            override fun onFailure(error: CredentialsManagerException) {
                WebAuthProvider.init(auth0)
                    .withScheme("app")
                    // Allow refresh tokens
                    .withScope("openid offline_access")
                    .withAudience(
                        String.format(
                            "https://%s/userinfo",
                            activity.getString(R.string.com_auth0_domain)
                        )
                    )
                    .start(activity, webCallback)
            }
        })
    }

    fun logout() {
        credentialsManager.clearCredentials()
        getKoin().getScope(KoinModules.USER_SCOPE_ID).close()
        Timber.d("Logged the user out")
    }

    private val webCallback = object : AuthCallback {
        override fun onFailure(dialog: Dialog) {
            loginSuccessListener?.onLoginFailure("onFailure called")
        }

        override fun onFailure(exception: AuthenticationException) {
            loginSuccessListener?.onLoginFailure(exception.localizedMessage)
        }

        override fun onSuccess(credentials: Credentials) {
            credentialsManager.saveCredentials(credentials)
            loginSuccessListener?.onLoginSuccess()
        }
    }

    interface LoginSuccessListener {
        fun onLoginSuccess()
        fun onLoginFailure(msg: String)
    }

    companion object {
        const val KEY_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
    }
}