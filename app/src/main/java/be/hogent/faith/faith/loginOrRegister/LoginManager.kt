package be.hogent.faith.faith.loginOrRegister

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import be.hogent.faith.R
import be.hogent.faith.util.TAG
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class LoginManager( private val loginCallback: LoginCallback
) : KoinComponent {

    /**
     * Authentication variables
     */
    private val auth0: Auth0 = get()

    private val credentialsManager: SecureCredentialsManager = get()

    init {
        auth0.isOIDCConformant = true
    }


    fun login(activity: Activity) {
        // Obtain the existing credentials and move to the next activity
        if(credentialsManager.hasValidCredentials()){
            Log.i(TAG,"Still Found credentials")
        }
        credentialsManager.getCredentials(object : BaseCallback<Credentials, CredentialsManagerException> {

            /**
             * User was already logged in
             */
            override fun onSuccess(credentials: Credentials) {
                loginCallback.onSuccess()

            }
            override fun onFailure(error: CredentialsManagerException) {
                WebAuthProvider.init(auth0)
                    .withScheme("app")
                    //Allow refresh tokens
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


    fun logout(){
        credentialsManager.clearCredentials()
        Log.d(TAG, "Logged the user out")
        //TODO: the part where the user object of the application should be set to null or something.
    }


    private val webCallback = object : AuthCallback {
        override fun onFailure(dialog: Dialog) {
            loginCallback.onFailure("onFailere called")
        }

        override fun onFailure(exception: AuthenticationException) {
            loginCallback.onFailure(exception.localizedMessage)
        }

        override fun onSuccess(credentials: Credentials) {
            credentialsManager.saveCredentials(credentials)
            loginCallback.onSuccess()
        }
    }


    interface LoginCallback {
        fun onSuccess()
        fun onFailure(msg: String)
    }

    companion object {
        const val KEY_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
    }
}