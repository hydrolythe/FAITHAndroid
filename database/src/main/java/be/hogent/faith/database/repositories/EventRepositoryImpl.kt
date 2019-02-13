package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

open class EventRepositoryImpl(
    private val eventDao: EventDao,
    private val detailDao: DetailDao,
    private val eventMapper: EventMapper
) : EventRepository {

    override fun delete(item: Event): Completable {
        return eventDao.delete(eventMapper.mapToEntity(item))
    }

    /**
     * Adds an item together as well as all its details
     *
     * @return a [Completable] that only succeeds when both the event and it details were inserted successfully.
     */
    override fun insert(item: Event): Completable {
        val eventEntity = eventMapper.mapToEntity(item)
        val eventCompletable = eventDao.insert(eventEntity)

        val detailMapper = DetailMapper(item)
        val detailsCompletable = detailDao.insertAll(item.details.map { detailMapper.mapToEntity(it) })
        return Completable.merge(listOf(eventCompletable, detailsCompletable))
    }

    /**
     * Adds all items as well as their details
     *
     * @return A [Completable] that only succeeds when all events and their details were inserted successfully.
     */
    override fun insertAll(items: List<Event>): Completable {
        val allCompletables = mutableListOf<Completable>()
        items.forEach { item -> allCompletables.add(insert(item)) }
        return Completable.merge(allCompletables)
    }

    override fun get(uuid: UUID): Flowable<Event> {
        val eventWithDetails = eventDao.getEventWithDetails(uuid)

        return eventWithDetails
            .map { combine(it) }
            .map { eventMapper.mapFromEntity(it) }
    }

    override fun getAll(): Flowable<List<Event>> {
        val eventsWithDetails = eventDao.getAllEventsWithDetails()
        return eventsWithDetails
            // TODO: learn more rxjava and find a way to not need combineList (map inside a map?)
            .map { combineList(it) }
            .map { eventMapper.mapFromEntities(it) }
    }

    /**
     * Takes an EventWithDetailsObject (a Room relation) and
     * puts it back together into a EventEntity with its details filled in.
     */
    private fun combine(eventWithDetails: EventWithDetails): EventEntity {
        val eventEntity = eventWithDetails.eventEntity!!
        val detailEntities = eventWithDetails.detailEntities
        eventEntity.details.addAll(detailEntities)
        return eventEntity
    }

    private fun combineList(eventsWithDetails: List<EventWithDetails>): List<EventEntity> {
        val result = mutableListOf<EventEntity>()
        eventsWithDetails.forEach {
            result.add(combine(it))
        }
        return result
    }
}