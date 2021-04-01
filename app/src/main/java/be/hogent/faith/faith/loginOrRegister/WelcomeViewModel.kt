package be.hogent.faith.faith.loginOrRegister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.state.Resource
import be.hogent.faith.faith.util.state.ResourceState
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.rxfirebase3.RxFirebaseAuth
import be.hogent.faith.util.TAG
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableMaybeObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class WelcomeViewModel(private val loginUserUseCase: LoginUserUseCase) : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _userLoggedInState = MutableLiveData<Resource<Unit>>()
    val userLoggedInState: LiveData<Resource<Unit>>
        get() = _userLoggedInState

    /**
     * reports errors with username
     */
    private val _userNameErrorMessage = MutableLiveData<Int>()
    val userNameErrorMessage: LiveData<Int>
        get() = _userNameErrorMessage

    /**
     * reports errors with password
     */
    private val _passwordErrorMessage = MutableLiveData<Int>()
    val passwordErrorMessage: LiveData<Int>
        get() = _passwordErrorMessage

    private val _registerButtonClicked = SingleLiveEvent<Unit>()
    val registerButtonClicked: LiveData<Unit>
        get() = _registerButtonClicked

    private val _feedbackButtonClicked = SingleLiveEvent<Unit>()
    val feedbackButtonClicked: LiveData<Unit>
        get() = _feedbackButtonClicked

    private val _reportBugButtonClicked = SingleLiveEvent<Unit>()
    val reportBugButtonClicked: LiveData<Unit>
        get() = _reportBugButtonClicked

    /**
     * user is new and wants to register
     */
    fun registerButtonClicked() {
        _registerButtonClicked.call()
    }

    /**
     * user wants to give feedback about the application
     */
    fun feedbackButtonClicked() {
        _feedbackButtonClicked.call()
    }

    /**
     * user wants to report a bug
     */
    fun reportButtonClicked() {
        _reportBugButtonClicked.call()
    }

    /**
     * logs in a user
     */
    fun loginButtonClicked() {
        login()
    }

    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            userName.value = ""
            _userLoggedInState.postValue(
                Resource(
                    ResourceState.ERROR,
                    null,
                    R.string.registerOrLogin_username_empty
                )
            )
            return false
        }
        _userNameErrorMessage.value = R.string.empty
        return true
    }

    private fun passwordIsValid(): Boolean {
        val pwd = password.value
        if (pwd.isNullOrBlank() || pwd.length < 6) {
            this.password.value = ""
            // _passwordErrorMessage.value = R.string.registerOrLogin_password_empty
            _userLoggedInState.postValue(
                Resource(
                    ResourceState.ERROR,
                    null,
                    R.string.registerOrLogin_password_empty
                )
            )
            return false
        }
        _passwordErrorMessage.value = R.string.empty
        return true
    }

    /**
     * validates username and password. Logs in the user
     */
    private fun login() {
        _userLoggedInState.postValue(Resource(ResourceState.LOADING, null, null))
        if (userNameIsValid() && passwordIsValid()) {
            val params = LoginUserUseCase.Params(userName.value!!, password.value!!)
            loginUserUseCase.execute(params, LoginUserUseCaseHandler())
        }
    }

    override fun onCleared() {
        loginUserUseCase.dispose()
        super.onCleared()
    }

    private inner class LoginUserUseCaseHandler : DisposableMaybeObserver<String?>() {
    // returns uuid when successfully logged in
    override fun onSuccess(t: String?) {
        _userLoggedInState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
    }

    override fun onComplete() {}

    override fun onError(e: Throwable) {
        Timber.e("$TAG: ${e.localizedMessage}")
        _userLoggedInState.postValue(
            Resource(
                ResourceState.ERROR, null, when (e) {
                    is InvalidCredentialsException -> R.string.login_error_wrong_username_or_password
                    is NetworkError -> R.string.login_error_internet
                    is SignInException -> R.string.login_error_wrong_username_or_password
                    else -> R.string.login_error
                }
            )
        )
    }
    }
}

class WeakPasswordException(error: Throwable) : RuntimeException(error)
class UserCollisionException(error: Throwable) : RuntimeException(error)
class InvalidCredentialsException(error: Throwable) : RuntimeException(error)
class SignInException(error: Throwable) : RuntimeException(error)
class SignOutException(error: Throwable) : RuntimeException(error)
class NetworkError(error: Throwable) : RuntimeException(error)

class LoginUserUseCase(
    private val authManager: IAuthManager,
    observer: Scheduler
) : MaybeUseCase<String?, LoginUserUseCase.Params>(observer) {

    override fun buildUseCaseMaybe(params: Params): Maybe<String?> {
        return authManager.signIn("${params.username}@faith.be", params.password)
    }

    data class Params(
        val username: String,
        val password: String
    )
}
abstract class MaybeUseCase<Result, in Params>(
    private val observer: Scheduler,
    protected val subscriber: Scheduler = Schedulers.io()
) {
    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseMaybe(params: Params): Maybe<Result>

    open fun execute(params: Params, singleObserver: DisposableMaybeObserver<Result>) {
        val single = this.buildUseCaseMaybe(params)
            .subscribeOn(subscriber)
            .observeOn(observer)
        addDisposable(single.subscribeWith(singleObserver))
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}

class AuthManager(private val firebase: FirebaseAuthManager):IAuthManager {
    override fun signIn(email: String, password: String) = firebase.signIn(email, password)
}
class FirebaseAuthManager(val auth:FirebaseAuth){
    fun signIn(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.signInWithEmailAndPassword(auth, email, password)
            .map { mapToUserUID(it) }
            .onErrorResumeNext { error: Throwable ->
                Timber.e("$TAG: signIn error ${error.message}")
                when (error) {
                    is FirebaseAuthInvalidCredentialsException -> Maybe.error(SignInException(error))
                    is FirebaseAuthInvalidUserException -> Maybe.error(SignInException(error))
                    is FirebaseException -> Maybe.error(NetworkError(error))
                    else -> Maybe.error(SignInException(error))
                }
            }
    }
    private fun mapToUserUID(result: AuthResult): String? {
        return result.user?.uid
    }
}
interface IAuthManager {

    fun signIn(email: String, password: String): Maybe<String?>

}