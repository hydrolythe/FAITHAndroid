package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Observable

interface UserRepository {

    fun delete(item: User): Completable

    fun insert(item: User): Completable

    fun get(uid: String): Observable<User>
}