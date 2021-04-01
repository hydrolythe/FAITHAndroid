package be.hogent.faith.faith.util.factory

import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.DrawingDetail

object DetailFactory {

    fun makeRandomDetail(): be.hogent.faith.faith.models.detail.Detail {
        val rand = Math.random()
        return when {
            rand < 0.33 -> makeTextDetail()
            rand < 0.66 -> makeDrawingDetail()
            else -> makeAudioDetail()
        }
    }

    fun makePhotoDetail(): be.hogent.faith.faith.models.detail.PhotoDetail =
        be.hogent.faith.faith.models.detail.PhotoDetail(
            DataFactory.randomFile(),
            DataFactory.randomString(),
            "",
            DataFactory.randomUUID()
        )

    fun makeTextDetail(): be.hogent.faith.faith.models.detail.TextDetail =
        be.hogent.faith.faith.models.detail.TextDetail(
            DataFactory.randomFile(),
            "",
            DataFactory.randomUUID()
        )

    fun makeDrawingDetail(): DrawingDetail =
        DrawingDetail(DataFactory.randomFile(), DataFactory.randomString(), "", DataFactory.randomUUID())

    fun makeAudioDetail(): AudioDetail =
        AudioDetail(DataFactory.randomFile(), "", DataFactory.randomUUID())
}