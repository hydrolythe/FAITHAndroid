package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseAuthManager
import be.hogent.faith.domain.repository.IAuthManager
import io.reactivex.Single

class AuthManager(private val firebase: FirebaseAuthManager) : IAuthManager {

    override fun isUsernameUnique(email: String): Single<Boolean> =
        firebase.isUsernameUnique(email)

    override fun signIn(email: String, password: String) = firebase.signIn(email, password)

    override fun register(email: String, password: String) = firebase.register(email, password)

    override fun signOut() = firebase.signOut()

    override fun getLoggedInUserUUID(): String? = firebase.getLoggedInUserUid()
}
