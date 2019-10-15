package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.UUID

open class EventRepositoryImpl(
    // Passing the database is required to run transactions across multiple DAO's.
    // This is required to insert an event together with all its details.
    private val userMapper: UserMapper,
    private val eventMapper: EventMapper,
    private val firebaseEventRepository: FirebaseEventRepository
) : EventRepository {
    // Although the DAO's could be injected, that would mean we inject both the
    // database and the DAO's, while the latter are made from the former, and are properties of it.
    // It's cleaner to just inject the database and request the daos during construction.

    override fun delete(item: Event, user: User): Completable {
        TODO("not implemented")
    }

    /**
     * Adds an item associated with a user together as well as all its details
     *
     * @return a [Completable] that only succeeds when both the event and it details were inserted successfully.
     */
    override fun insert(item: Event, user: User): Maybe<Event> {
        return firebaseEventRepository.insert(
            eventMapper.mapToEntity(item),
            userMapper.mapToEntity(user)
        ).map { eventMapper.mapFromEntity(it) }
    }

    /**
     * Adds all items associated with a user as well as their details
     *
     * @return A [Completable] that only succeeds when all events and their details were inserted successfully.
     */
    override fun insertAll(items: List<Event>, user: User): Completable {
        TODO("not implemented")
    }

    override fun get(uuid: UUID): Flowable<Event> {
        return firebaseEventRepository.get(uuid.toString()).map { eventMapper.mapFromEntity(it) }
    }

    override fun getAll(user: User): Flowable<List<Event>> {
        return firebaseEventRepository.getAll().map { eventMapper.mapFromEntities(it) }
    }
}