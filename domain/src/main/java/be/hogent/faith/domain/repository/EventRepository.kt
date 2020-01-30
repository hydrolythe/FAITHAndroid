package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import java.util.Observable
import java.util.UUID

interface EventRepository {
    fun delete(event: Event, user: User): Completable

    /**
     * Saves the given [event] to the events of the given [user]
     */
    // TODO: fix that this returns a Maybe<Event> because that seems like poor API design
    fun insert(event: Event, user: User): Completable

    fun get(uuid: UUID): io.reactivex.Observable<Event>

    fun getAll(): io.reactivex.Observable<List<Event>>
}