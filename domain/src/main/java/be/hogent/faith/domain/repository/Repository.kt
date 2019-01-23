package be.hogent.faith.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

interface Repository<T> {
    fun deleteAll()

    fun delete(item: T)

    fun add(item: T):Completable

    fun addAll(items: List<T>)

    fun get(uuid: UUID): Flowable<T>

    fun getAll(): Flowable<List<T>>
}
