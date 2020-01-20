package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.IEventEntityEncrypter
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.storage.ILocalStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

open class EventRepositoryImpl(
    private val userMapper: UserMapper,
    private val eventMapper: EventMapper,
    private val localStorageRepository: ILocalStorageRepository,
    private val firebaseEventRepository: FirebaseEventRepository,
    private val eventEntityEncrypter: IEventEntityEncrypter
) : EventRepository {

    /**
     * gets the event with uuid for authenticated user
     */
    override fun get(uuid: UUID): Flowable<Event> {
        return firebaseEventRepository.get(uuid.toString())
            .map { eventEntityEncrypter.decrypt(it) }
            .map { eventMapper.mapFromEntity(it) }
    }

    /**
     * Adds an event for the authenticated user together with its details
     * @return a [Maybe<Event>] that only succeeds when both the event and its details are inserted successfully.
     */
    override fun insert(event: Event, user: User): Maybe<Event> {
        val eventEntity = eventMapper.mapToEntity(event)
        val encryptedEvent = eventEntityEncrypter.encrypt(event, eventEntity)

        return localStorageRepository.saveEvent(encryptedEvent).andThen(
            firebaseEventRepository.insert(
                encryptedEvent,
                userMapper.mapToEntity(user)
            ).map { event }
        )
    }

    /**
     * get all events for authenticated user
     */
    override fun getAll(): Flowable<List<Event>> {
        return firebaseEventRepository.getAll()
            .map { eventEntityEncrypter.decryptAll(it) }
            .map { eventMapper.mapFromEntities(it) }
    }

    override fun delete(event: Event, user: User): Completable {
        TODO("not implemented")
    }
}