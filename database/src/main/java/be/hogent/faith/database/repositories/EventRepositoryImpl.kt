package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.storage.ILocalStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

open class EventRepositoryImpl(
    private val localStorageRepository: ILocalStorageRepository,
    private val firebaseEventRepository: FirebaseEventRepository,
    private val eventEncryptionService: EventEncryptionServiceInterface
) : EventRepository {

    private val userMapper = UserMapper
    private val eventMapper = EventMapper
    /**
     * gets the event with uuid for authenticated user
     */
    override fun get(uuid: UUID): Flowable<Event> {
        return firebaseEventRepository.get(uuid.toString())
            .map { eventMapper.mapFromEntity(it) }
            .map { eventEncryptionService.decrypt(it) }
    }

    /**
     * Adds an event for the authenticated user together with its details
     * @return a [Maybe<Event>] that only succeeds when both the event and its details are inserted successfully.
     */
    override fun insert(event: Event, user: User): Completable {
        // TODO: maybe not mix procedural and reactive? Might lead to bugs.
        val encryptedEvent = eventEncryptionService.encrypt(event)
        // Move to local storage -> paths inside the event are changed, and final
        localStorageRepository.saveEvent(encryptedEvent)

        val eventEntity = eventMapper.mapToEntity(encryptedEvent)

        return firebaseEventRepository.insert(
            eventEntity,
            userMapper.mapToEntity(user)
        )
    }

    /**
     * get all events for authenticated user
     */
    override fun getAll(): Flowable<List<Event>> {
        return firebaseEventRepository.getAll()
            .map { eventMapper.mapFromEntities(it) }
            .map { eventEncryptionService.decryptAll(it) }
    }

    override fun delete(event: Event, user: User): Completable {
        TODO("not implemented")
    }
}