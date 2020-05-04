package be.hogent.faith.database.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.service.repositories.IEventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.UUID

open class EventRepository(
    private val eventDatabase: EventDatabase
) : IEventRepository {

    private val eventMapper = EventMapper

    /**
     * Gets the event with the given [uuid] for the currently authenticated user.
     * Does not download the files belonging to the event!
     */
    override fun get(uuid: UUID): Flowable<EncryptedEvent> {
        return eventDatabase.get(uuid)
            .map(eventMapper::mapFromEntity)
    }

    override fun update(encryptedEvent: EncryptedEvent): Completable {
        return Single.just(encryptedEvent)
            .map(eventMapper::mapToEntity)
            .flatMapCompletable(eventDatabase::update)
    }

    /**
     * Adds an event for the authenticated user together with its details
     * @return a [Maybe<Event>] that only succeeds when both the event and its details are inserted successfully.
     */
    override fun insert(encryptedEvent: EncryptedEvent): Completable {
        return Single.just(encryptedEvent)
            .map(eventMapper::mapToEntity)
            .flatMapCompletable(eventDatabase::insert)
    }

    override fun delete(event: Event, user: User): Completable {
        TODO("not implemented")
    }

    override fun getAll(): Flowable<List<EncryptedEvent>> {
        return eventDatabase.getAll()
            .map(eventMapper::mapFromEntities)
    }
}