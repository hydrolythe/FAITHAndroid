package be.hogent.faith.database.factory

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import java.util.*


object DetailFactory {
    fun makeDetailEntity(eventUuid : UUID): DetailEntity {
        return DetailEntity(DataFactory.randomUID(),
            eventUuid, DetailTypeEntity.DRAWING)
    }

    fun makeDetail(): Detail {
        return Detail(DetailType.DRAWING, DataFactory.randomUID() )
    }

    fun makeDetailEntityList(count: Int, eventUuid:UUID) : List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity(eventUuid))
        }
        return details
    }


}