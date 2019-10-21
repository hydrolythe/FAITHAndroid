package be.hogent.faith.domain.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface AuthManager {
    fun isUsernameUnique(email: String): Single<Boolean>

    fun register(email: String, password: String): Maybe<String?>

    fun signIn(email: String, password: String): Maybe<String?>

    fun signOut(): Completable

    fun reset()

    fun getLoggedInUser(): String?

    val currentUser: BehaviorSubject<String>
}

class WeakPasswordException(error: Throwable) : RuntimeException(error)
class UserCollisionException(error: Throwable) : RuntimeException(error)
class InvalidCredentialsException(error: Throwable) : RuntimeException(error)
class SignInException(error: Throwable) : RuntimeException(error)
class SignOutException(error: Throwable) : RuntimeException(error)
class NetworkError(error: Throwable) : RuntimeException(error)