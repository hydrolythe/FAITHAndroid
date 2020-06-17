package be.hogent.faith.service.repositories

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface IAuthManager {
    fun isUsernameUnique(email: String): Single<Boolean>

    /**
     * Registers a user with the given [email] and [password]
     * @return the users UUID>
     */
    fun register(email: String, password: String): Maybe<String?>

    fun signIn(email: String, password: String): Maybe<String?>

    fun signOut(): Completable

    fun getLoggedInUserUUID(): String?
}

class WeakPasswordException(error: Throwable) : RuntimeException(error)
class UserCollisionException(error: Throwable) : RuntimeException(error)
class InvalidCredentialsException(error: Throwable) : RuntimeException(error)
class SignInException(error: Throwable) : RuntimeException(error)
class SignOutException(error: Throwable) : RuntimeException(error)
class NetworkError(error: Throwable) : RuntimeException(error)