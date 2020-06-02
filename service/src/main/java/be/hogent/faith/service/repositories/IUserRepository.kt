package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Flowable

interface IUserRepository {

    fun delete(user: User): Completable

    fun initialiseUser(user: User): Completable

    fun get(uid: String): Flowable<User>
}