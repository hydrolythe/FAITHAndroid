package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseAuthManager
import be.hogent.faith.domain.repository.AuthManager
import io.reactivex.Single

class AuthManagerImpl(private val firebase: FirebaseAuthManager) : AuthManager {

    override fun isUsernameUnique(email: String): Single<Boolean> =
        firebase.isUsernameUnique(email)

    override fun signIn(email: String, password: String) = firebase.signIn(email, password)

    override fun register(email: String, password: String) = firebase.register(email, password)

    override fun signOut() = firebase.signOut()

    override fun reset() {
        // TODO ("not implemented")
    }

    override fun getLoggedInUserUUID(): String? = firebase.getLoggedInUser()
}
