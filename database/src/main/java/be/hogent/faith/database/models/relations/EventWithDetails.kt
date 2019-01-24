package be.hogent.faith.database.models.relations

import androidx.room.Embedded
import androidx.room.Relation
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.EventEntity

class EventWithDetails(
    @Embedded
    var eventEntity: EventEntity? = null,
    @Relation(parentColumn = "uuid", entityColumn = "eventUuid", entity = DetailEntity::class)
    var detailEntities: List<DetailEntity> = mutableListOf()
)