package be.hogent.faith.database.firebase

import be.hogent.faith.domain.repository.InvalidCredentialsException
import be.hogent.faith.domain.repository.NetworkError
import be.hogent.faith.domain.repository.SignInException
import be.hogent.faith.domain.repository.SignOutException
import be.hogent.faith.domain.repository.UserCollisionException
import be.hogent.faith.domain.repository.WeakPasswordException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

/**
 * uses RxFirebaseAuth : https://github.com/FrangSierra/RxFirebase
 */
class FirebaseAuthManager(
    private val auth: FirebaseAuth
) {

    // TODO:klopt dit wel
    private var authStateListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null && it.currentUser is FirebaseUser)
            loggedInUser.onNext((it.currentUser as FirebaseUser).uid)
        else
            loggedInUser.onNext("emptyUser")
    }

    init {
        auth.addAuthStateListener { authStateListener }
    }

    var loggedInUser: BehaviorSubject<String> = BehaviorSubject.create()

    fun getLoggedInUser(): String? {
        return auth.currentUser?.uid
    }

    fun checkIfEmailExists(email: String): Single<Boolean> {
        return RxFirebaseAuth.fetchSignInMethodsForEmail(auth, email)
            .map { if (it.signInMethods?.size == 0) true else false }
            .onErrorResumeNext { e: Throwable ->
                when (e) {
                    is FirebaseException -> Maybe.error(NetworkError(e))
                    else -> Maybe.error(RuntimeException("Error fetching providers for email", e))
                }
            }
            .toSingle()
    }

    fun register(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.createUserWithEmailAndPassword(auth, email, password)
            .map { mapToUser(it) }
            // map FirebaseExceptions to domain exceptions
            .onErrorResumeNext { error: Throwable ->
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

    fun signIn(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.signInWithEmailAndPassword(auth, email, password)
            .map { mapToUser(it) }
            .onErrorResumeNext { error: Throwable ->
                when (error) {
                    is FirebaseAuthInvalidCredentialsException -> Maybe.error(SignInException(error))
                    is FirebaseException -> Maybe.error(NetworkError(error))
                    else -> Maybe.error(SignInException(error))
                }
            }
    }

    fun signOut(): Completable {
        return try {
            auth.signOut()
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(SignOutException(e))
        }
    }

    fun reset() {
        throw NotImplementedError()
    }

    private fun mapToUser(result: AuthResult): String? {
        return result.user?.uid
    }

    private fun mapToUser(result: FirebaseUser?): String? {
        return result?.uid
    }

    protected fun finalize() {
        disposeAuthListener()
    }

    private fun disposeAuthListener() {
        auth.removeAuthStateListener(authStateListener)
    }
}