package be.hogent.faith.database.models.relations

import androidx.room.Embedded
import androidx.room.Relation
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.EventEntity

/**
 * Relation class for the [EventEntity] and it's relation with [DetailEntity]s
 */
class EventWithDetails {
    @Embedded
    lateinit var eventEntity: EventEntity
    @Relation(parentColumn = "uuid", entityColumn = "eventUuid", entity = DetailEntity::class)
    lateinit var detailEntities: List<DetailEntity>
}