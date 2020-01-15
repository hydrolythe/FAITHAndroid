package be.hogent.faith.service.usecases.dummies

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

class SingleUserDummyEventRepository : EventRepository {
    private val events = mutableListOf<Event>()

    override fun delete(event: Event, user: User): Completable {
        return Completable.fromCallable {
            events.remove(event)
        }
    }

    override fun insert(event: Event, user: User): Maybe<Event> {
        events.add(event)
        return Maybe.just(event)
    }

    override fun get(uuid: UUID): Flowable<Event> {
        return Flowable.just(events.first { it.uuid == uuid })
    }

    override fun getAll(): Flowable<List<Event>> {
        return Flowable.just(events)
    }

}