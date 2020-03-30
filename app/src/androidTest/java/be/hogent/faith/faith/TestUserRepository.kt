package be.hogent.faith.faith

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.IUserRepository
import be.hogent.faith.util.factory.UserFactory
import io.reactivex.Completable
import io.reactivex.Observable

class TestUserRepository : IUserRepository {
    override fun delete(item: User): Completable {
        return Completable.complete()
    }

    override fun insert(item: User): Completable {
        return Completable.complete()
    }

    override fun get(uid: String): Observable<User> {
        return Observable.just(UserFactory.makeUser())
    }
}
