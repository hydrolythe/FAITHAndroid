package be.hogent.faith.database.authentication

import be.hogent.faith.service.repositories.InvalidCredentialsException
import be.hogent.faith.service.repositories.NetworkError
import be.hogent.faith.service.repositories.SignInException
import be.hogent.faith.service.repositories.SignOutException
import be.hogent.faith.service.repositories.UserCollisionException
import be.hogent.faith.service.repositories.WeakPasswordException
import be.hogent.faith.util.TAG
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

/**
 * uses RxFirebaseAuth : https://github.com/FrangSierra/RxFirebase
 */
class FirebaseAuthManager(
    private val auth: FirebaseAuth
) {

    /**
     * uid of authenticated user
     */
    fun getLoggedInUserUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * check if email is unique when user wants to register
     */
    fun isUsernameUnique(email: String): Single<Boolean> {
        return RxFirebaseAuth.fetchSignInMethodsForEmail(auth, email)
            .map { it.signInMethods?.size == 0 }
            .onErrorResumeNext { error: Throwable ->
                Timber.e("$TAG: error when looking up if username is unique ${error.message}")
                when (error) {
                    is FirebaseException -> Maybe.error(NetworkError(error))
                    else -> Maybe.error(
                        RuntimeException(
                            "Error fetching providers for email",
                            error
                        )
                    )
                }
            }
            .toSingle()
    }

    /**
     * register the user
     */
    fun register(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.createUserWithEmailAndPassword(auth, email, password)
            .map { mapToUserUID(it) }
            // map FirebaseExceptions to domain exceptions
            .onErrorResumeNext { error: Throwable ->
                Timber.e("$TAG: error registering ${error.message}")
                when (error) {
                    is FirebaseAuthWeakPasswordException -> Maybe.error(WeakPasswordException(error))
                    is FirebaseAuthUserCollisionException -> Maybe.error(
                        UserCollisionException(
                            error
                        )
                    )
                    is FirebaseAuthInvalidCredentialsException -> Maybe.error(
                        InvalidCredentialsException(error)
                    )
                    is FirebaseException -> Maybe.error(NetworkError(error))
                    else -> Maybe.error(Throwable(error))
                }
            }
    }

    /**
     * sign in the user
     */
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

    /**
     * sign out the user
     */
    fun signOut(): Completable {
        return try {
            auth.signOut()
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(SignOutException(e))
        }
    }

    private fun mapToUserUID(result: AuthResult): String? {
        return result.user?.uid
    }
}