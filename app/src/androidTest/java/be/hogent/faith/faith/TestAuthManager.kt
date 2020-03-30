package be.hogent.faith.faith

import be.hogent.faith.domain.repository.IAuthManager
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

class TestAuthManager : IAuthManager {
    override fun isUsernameUnique(email: String): Single<Boolean> {
        return Single.just(true)
    }

    override fun register(email: String, password: String): Maybe<String?> {
        return Maybe.just(email)
    }

    override fun signIn(email: String, password: String): Maybe<String?> {
        return Maybe.just(email)
    }

    override fun signOut(): Completable {
        return Completable.complete()
    }

    override fun getLoggedInUserUUID(): String? {
        return UUID.randomUUID().toString()
    }
}