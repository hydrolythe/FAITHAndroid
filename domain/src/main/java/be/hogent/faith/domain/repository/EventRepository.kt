package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

interface EventRepository {
    fun delete(item: Event, user: User): Completable

    fun insert(item: Event, user: User): Maybe<Event>

    fun insertAll(items: List<Event>, user: User): Completable

    fun get(uuid: UUID): Flowable<Event>

    fun getAll(user: User): Flowable<List<Event>>
}