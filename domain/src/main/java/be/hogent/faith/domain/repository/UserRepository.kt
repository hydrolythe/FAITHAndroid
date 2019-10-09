package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface UserRepository {

    fun delete(item: User): Completable

    fun insert(item: User): Completable

    fun get(uuid: String): Flowable<User>

    fun getAll(): Flowable<List<User>>

    fun isUsernameUnique(username: String): Single<Boolean>
}