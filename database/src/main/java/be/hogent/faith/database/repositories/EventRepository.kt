package be.hogent.faith.database.repositories

import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import io.reactivex.Flowable
import java.util.UUID

class EventRepository(
    private val database: EntityDatabase,
    private val eventMapper: EventMapper
) : Repository<Event> {

    private val eventDao = database.eventDao()
    private val detailDao = database.detailDao()

    override fun deleteAll() {
        eventDao.deleteAll()
    }

    override fun delete(item: Event) {
        eventDao.delete(eventMapper.mapToEntity(item))
    }

    /**
     * Adds an item together as well as all its details
     */
    override fun add(item: Event) {
        val eventEntity = eventMapper.mapToEntity(item)
        eventDao.insert(eventEntity)

        val detailMapper = DetailMapper(item)
        item.details.forEach {
            val detailEntity = detailMapper.mapToEntity(it)
            detailDao.insert(detailEntity)
        }
    }

    override fun addAll(items: List<Event>) {
        items.forEach { item -> add(item) }
    }

    override fun get(uuid: UUID): Flowable<Event> {
        val eventWithDetails = eventDao.getEventWithDetails(uuid)

        return eventWithDetails
            .map { it -> combine(it) }
            .map { it -> EventMapper.mapFromEntity(it) }
    }

    override fun getAll(): Flowable<List<Event>> {
        val eventsWithDetails = eventDao.getAllEventsWithDetails()
        return eventsWithDetails
            //TODO: learn more rxjava and find a way to not need combineList (map inside a map?)
            .map { it -> combineList(it) }
            .map { it -> EventMapper.mapFromEntities(it) }
    }

    /**
     * Takes an EventWithDetailsObject (a Room relation) and
     * puts it back together into a EventEntity with its details filled in
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