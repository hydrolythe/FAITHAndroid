package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.util.factory.DataFactory.randomString

object DetailFactory {

    fun makeDetail(): Detail {
        val rand = Math.random()
        return when {
            rand < 0.33 -> TextDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID()
            )
            rand < 0.66 -> PictureDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID()
            )
            else -> AudioDetail(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID()
            )
        }
    }
}