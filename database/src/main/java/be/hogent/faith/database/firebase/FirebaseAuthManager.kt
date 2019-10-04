package be.hogent.faith.database.firebase

import android.net.Uri
import android.util.Log
import be.hogent.faith.domain.repository.InvalidCredentialsException
import be.hogent.faith.domain.repository.SignInException
import be.hogent.faith.domain.repository.SignOutException
import be.hogent.faith.domain.repository.UserCollisionException
import be.hogent.faith.domain.repository.WeakPasswordException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.subjects.BehaviorSubject

class FirebaseAuthManager {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    //TODO:klopt dit wel
    private var authStateListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null && it.currentUser is FirebaseUser)
            loggedInUser.onNext((it.currentUser as FirebaseUser).uid)
        else
            loggedInUser.onNext("")
    }

    init {
        auth.addAuthStateListener { authStateListener }
    }

    var loggedInUser: BehaviorSubject<String> = BehaviorSubject.create()

    fun register(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.createUserWithEmailAndPassword(auth, email, password)
            .map { mapToUser(it) }
            //map FirebaseExceptions to domain exceptions
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
                    else -> Maybe.error(Throwable(error))
                }
            }
    }


    fun signIn(email: String, password: String): Maybe<String?> {
        return RxFirebaseAuth.signInWithEmailAndPassword(auth, email, password)
            .map { mapToUser(it) }
            .onErrorResumeNext { error: Throwable -> Maybe.error(SignInException(error)) }
        /*
        FirebaseAuthInvalidUserException thrown if the user account corresponding to email does not exist or has been disabled
        FirebaseAuthInvalidCredentialsException thrown if the password is wrong
         */
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

    fun disposeAuthListener() {
        auth.removeAuthStateListener(authStateListener)
    }
}