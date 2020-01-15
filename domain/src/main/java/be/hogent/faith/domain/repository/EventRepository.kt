package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

interface EventRepository {
    fun delete(event: Event, user: User): Completable

    /**
     * Saves the given [event] to the events of the given [user]
     */
    // TODO: fix that this returns a Maybe<Event> because that seems like poor API design
    fun insert(event: Event, user: User): Maybe<Event>

    fun get(uuid: UUID): Flowable<Event>

    fun getAll(): Flowable<List<Event>>
}