package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.util.factory.EventFactory
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

class TestEventRepository : EventRepository {
    override fun delete(item: Event, user: User): Completable {
        return Completable.complete()
    }

    override fun insert(item: Event, user: User): Maybe<Event> {
        return Maybe.just(EventFactory.makeEvent())
    }

    override fun get(uuid: UUID): Flowable<Event> {
        return Flowable.just(EventFactory.makeEvent())
    }

    override fun getAll(): Flowable<List<Event>> {
        return Flowable.just(EventFactory.makeEventList())
    }
}