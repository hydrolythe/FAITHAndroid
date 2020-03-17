package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.IEventEncryptionService
import be.hogent.faith.database.firebase.EventDatabase
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
    private val eventDatabase: EventDatabase,
    private val eventEncryptionService: IEventEncryptionService
) : EventRepository {

    private val userMapper = UserMapper
    private val eventMapper = EventMapper

    /**
     * Gets the event with the given [uuid] for the currently authenticated user.
     * Also downloads all files associated with it, so it is ready to be used.
     */
    override fun get(uuid: UUID): Observable<Event> {
        return firebaseEventRepository.get(uuid.toString())
            .map(eventMapper::mapFromEntity)
            .flatMapSingle(eventEncryptionService::decrypt)
            .flatMap { fileStorageRepository.downloadEventFiles(it).andThen(Observable.just(it)) }
    }

    /**
     * Adds an event for the authenticated user together with its details
     * @return a [Maybe<Event>] that only succeeds when both the event and its details are inserted successfully.
     */
    override fun insert(event: Event, user: User): Completable {
        return eventEncryptionService.encrypt(event)
            .flatMap(fileStorageRepository::saveEventFiles)
            .map(eventMapper::mapToEntity)
            .flatMapCompletable { entity ->
                eventDatabase.insert(entity, userMapper.mapToEntity(user))
            }
    }

    /**
     * Get all events for the authenticated user.
     * Does <b>NOT</b> download the files for each event. This will have to be done separately
     * using [EventRepositoryImpl.get].
     */
    override fun getAll(): Observable<List<Event>> {
        return eventDatabase.getAll()
            .map { eventMapper.mapFromEntities(it) }
            .flatMapSingle(eventEncryptionService::decryptList)
            .toObservable()
    }

    override fun delete(event: Event, user: User): Completable {
        TODO("not implemented")
    }
}