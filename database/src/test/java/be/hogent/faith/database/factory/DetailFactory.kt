package be.hogent.faith.database.factory

import be.hogent.faith.database.factory.DataFactory.randomString
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import java.util.UUID

object DetailFactory {
    fun makeDetailEntity(eventUuid: UUID): DetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.33 -> TextDetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID(), eventUuid
            )
            rand < 0.66 -> PictureDetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID(), eventUuid
            )
            else -> AudioDetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID(), eventUuid
            )
        }
    }

    fun makeDetail(): Detail {
        val rand = Math.random()
        return when {
            rand < 0.33 -> TextDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID()
            )
            rand < 0.66 -> PictureDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID()
            )
            else -> AudioDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUID()
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