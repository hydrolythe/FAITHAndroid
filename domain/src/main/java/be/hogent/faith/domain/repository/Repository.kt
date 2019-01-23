package be.hogent.faith.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

interface Repository<T> {
    fun deleteAll()

    fun delete(item: T)

    fun insert(item: T):Completable

    fun insertAll(items: List<T>) : Completable

    fun get(uuid: UUID): Flowable<T>

    fun getAll(): Flowable<List<T>>
}
