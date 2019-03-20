package be.hogent.faith.database.factory

import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import java.util.UUID

object DetailFactory {
    fun makeDetailEntity(eventUuid: UUID): DetailEntity {
        return DetailEntity(
            DataFactory.randomFile(),
            DataFactory.randomUID(),
            eventUuid
        )
    }

    fun makeDetail(): Detail {
        val rand = Math.random()
        return when {
            rand < 0.33 -> TextDetail(
                DataFactory.randomFile(), DataFactory.randomUID()
            )
            rand < 0.66 -> PictureDetail(
                DataFactory.randomFile(), DataFactory.randomUID()
            )
            else -> AudioDetail(
                DataFactory.randomFile(), DataFactory.randomUID()
            )
        }
    }

    fun makeDetailEntityList(count: Int, eventUuid: UUID): List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity(eventUuid))
        }
        return details
    }
}