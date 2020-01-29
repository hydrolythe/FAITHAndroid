package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.UUID

open class EventRepositoryImpl(
    private val fileStorageRepository: IFileStorageRepository,
    private val firebaseEventRepository: FirebaseEventRepository,
    private val eventEncryptionService: EventEncryptionServiceInterface
) : EventRepository {

    private val userMapper = UserMapper
    private val eventMapper = EventMapper

    /**
     * Gets the event with uuid for authenticated user
     * **Does not download the events files!**
     */
    // TODO: verschil tussen event ophalen (data) en event met files ophalen
    override fun get(uuid: UUID): Observable<Event> {
        return firebaseEventRepository.get(uuid.toString())
            .map(eventMapper::mapFromEntity)
            .flatMapSingle(eventEncryptionService::decrypt)
    }

    /**
     * Adds an event for the authenticated user together with its details
     * @return a [Maybe<Event>] that only succeeds when both the event and its details are inserted successfully.
     */
    override fun insert(event: Event, user: User): Completable {
        return eventEncryptionService.encrypt(event)
            .flatMap(fileStorageRepository::saveEvent)
            .map(eventMapper::mapToEntity)
            .flatMapCompletable { entity ->
                firebaseEventRepository.insert(
                    entity,
                    userMapper.mapToEntity(user)
                )
            }
    }

    /**
     * get all events for authenticated user
     */
    override fun getAll(): Observable<List<Event>> {
        return firebaseEventRepository.getAll()
            .map { eventMapper.mapFromEntities(it) }
            .flatMapSingle(eventEncryptionService::decryptList)
            .toObservable()
    }

    override fun delete(event: Event, user: User): Completable {
        TODO("not implemented")
    }
}