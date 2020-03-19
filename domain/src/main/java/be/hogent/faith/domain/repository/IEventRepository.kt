package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.UUID

interface IEventRepository {
    fun delete(event: Event, user: User): Completable

    /**
     * Saves the given [event] to the events of the given [user]
     */
    fun insert(event: Event, user: User): Completable

    fun getEventData(uuid: UUID): Observable<Event>

    fun getAllEventsData(): Observable<List<Event>>

    fun makeEventFilesAvailable(event: Event): Completable
}