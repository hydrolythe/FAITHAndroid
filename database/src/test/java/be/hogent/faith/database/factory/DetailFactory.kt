package be.hogent.faith.database.factory

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import java.util.UUID

object DetailFactory {
    fun makeDetailEntity(eventUuid: UUID): DetailEntity {
        return DetailEntity(
            DetailTypeEntity.DRAWING,
            DataFactory.randomFile(),
            DataFactory.randomUID(),
            eventUuid
        )
    }

    fun makeDetail(): Detail {
        return Detail(
            DetailType.DRAWING,
            DataFactory.randomFile(),
            DataFactory.randomUID()
        )
    }

    fun makeDetailEntityList(count: Int, eventUuid: UUID): List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity(eventUuid))
        }
        return details
    }
}