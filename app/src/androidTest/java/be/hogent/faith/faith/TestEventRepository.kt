package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.util.factory.EventFactory
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.UUID

class TestEventRepository : IEventRepository {
    override fun delete(item: Event, user: User): Completable {
        return Completable.complete()
    }

    override fun insert(item: Event, user: User): Completable {
        return Completable.complete()
    }

    override fun getEventData(uuid: UUID): Observable<Event> {
        return Observable.just(EventFactory.makeEvent())
    }

    override fun getAll(): Observable<List<Event>> {
        return Observable.just(EventFactory.makeEventList())
    }

    override fun makeEventFilesAvailable(uuid: UUID): Observable<Event> {
        return Observable.just(EventFactory.makeEvent())
    }
}