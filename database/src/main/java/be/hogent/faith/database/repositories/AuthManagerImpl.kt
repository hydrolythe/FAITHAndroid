package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseAuthManager
import be.hogent.faith.domain.repository.AuthManager

class AuthManagerImpl(private val firebase: FirebaseAuthManager) : AuthManager {

    override fun signIn(email: String, password: String) = firebase.signIn(email, password)

    override fun register(email: String, password: String) = firebase.register(email, password)

    override fun signOut() = firebase.signOut()

    override fun reset() {

    }
}
