package be.hogent.faith.domain.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

interface AuthManager {
    fun register(email: String, password: String): Maybe<String>

    fun signIn(email: String, password: String): Maybe<String>

    fun signOut(): Completable

    fun isUnique(email:String): Single<Boolean>

    fun reset()
}