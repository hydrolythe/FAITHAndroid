package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.util.factory.DataFactory.randomString

object DetailFactory {

    fun makeRandomDetail(): Detail {
        val rand = Math.random()
        return when {
            rand < 0.33 -> makeTextDetail()
            rand < 0.66 -> makePictureDetail()
            else -> makeAudioDetail()
        }
    }

    fun makeTextDetail(): TextDetail =
        TextDetail(DataFactory.randomFile(), randomString(), DataFactory.randomUUID())

    fun makePictureDetail(): PictureDetail =
        PictureDetail(DataFactory.randomFile(), randomString(), DataFactory.randomUUID())

    fun makeAudioDetail(): AudioDetail =
        AudioDetail(DataFactory.randomFile(), randomString(), DataFactory.randomUUID())
}