package be.hogent.faith.service.usecases.fakes

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.repositories.IEventRepository
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.UUID

class SingleUserFakeEventRepository : IEventRepository {
    private val events = mutableListOf<Event>()

    override fun delete(event: Event, user: User): Completable {
        return Completable.fromCallable {
            events.remove(event)
        }
    }

    override fun insert(event: Event, user: User): Completable {
        return Completable.fromCallable {
            events.add(event)
        }
    }

    override fun get(uuid: UUID): Observable<Event> {
        return Observable.just(events.first { it.uuid == uuid })
    }

    override fun getAllEventsData(): Observable<List<Event>> {
        return Observable.just(events)
    }

    override fun makeEventFilesAvailable(event: Event): Completable {
        return Completable.complete()
    }
}