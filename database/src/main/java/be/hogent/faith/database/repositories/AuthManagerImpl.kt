package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseAuthManager
import be.hogent.faith.domain.repository.AuthManager
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class AuthManagerImpl(private val firebase: FirebaseAuthManager) : AuthManager {

    override fun checkIfEmailExists(email: String): Single<Boolean> =
        firebase.checkIfEmailExists(email)

    override fun signIn(email: String, password: String) = firebase.signIn(email, password)

    override fun register(email: String, password: String) = firebase.register(email, password)

    override fun signOut() = firebase.signOut()

    override fun reset() {}

    override fun getLoggedInUser(): String? = firebase.getLoggedInUser()

    override val currentUser: BehaviorSubject<String>
        get() = firebase.loggedInUser
}
