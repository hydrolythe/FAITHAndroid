package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.EventWithDetailsMapper
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

open class EventRepositoryImpl(
    // Passing the database is required to run transactions across multiple DAO's.
    // This is required to insert an event together with all its details.
    private val database: EntityDatabase,
    private val eventMapper: EventMapper,
    private val eventWithDetailsMapper: EventWithDetailsMapper,
    private val detailMapper: DetailMapper
) : EventRepository {
    // Although the DAO's could be injected, that would mean we inject both the
    // database and the DAO's, while the latter are made from the former, and are properties of it.
    // It's cleaner to just inject the database and request the daos during construction.
    private val eventDao: EventDao = database.eventDao()
    private val detailDao: DetailDao = database.detailDao()

    override fun delete(item: Event, user: User): Completable {
        return eventDao.delete(eventMapper.mapToEntity(item, user.uuid))
    }

    /**
     * Adds an item associated with a user together as well as all its details
     *
     * @return a [Completable] that only succeeds when both the event and it details were inserted successfully.
     */
    override fun insert(item: Event, user: User): Completable {
        return try {
            database.runInTransaction {
                eventDao.insert(eventMapper.mapToEntity(item, user.uuid))
                detailDao.insertAll(item.details.map { detailMapper.mapToEntity(it, item.uuid) })
            }
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(e)
        }
    }

    /**
     * Adds all items associated with a user as well as their details
     *
     * @return A [Completable] that only succeeds when all events and their details were inserted successfully.
     */
    override fun insertAll(items: List<Event>, user: User): Completable {
        return Completable.merge(
            items.map { item -> insert(item, user) }
        )
    }

    override fun get(uuid: UUID): Flowable<Event> {
        val eventWithDetails = eventDao.getEventWithDetails(uuid)

        return eventWithDetails
            .map { eventWithDetailsMapper.mapFromEntity(it) }
    }

    override fun getAll(user: User): Flowable<List<Event>> {
        val eventsWithDetails = eventDao.getAllEventsWithDetails(user.uuid)
        return eventsWithDetails
            .map { eventWithDetailsMapper.mapFromEntities(it) }
    }
}