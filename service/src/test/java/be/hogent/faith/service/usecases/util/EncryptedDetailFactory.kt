package be.hogent.faith.service.usecases.util

import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.util.factory.DataFactory

object EncryptedDetailFactory {

    fun makeRandomDetail() = EncryptedDetail(
        file = DataFactory.randomFile(),
        title = DataFactory.randomString(),
        uuid = DataFactory.randomUUID(),
        type = DataFactory.randomString(),
        dateTime = DataFactory.randomString(),
        thumbnail = DataFactory.randomString(),
        // Empty to indicate this is not a YoutubeVideoDetail
        youtubeVideoID = ""
    )

    fun makeYoutbeVideoDetail() = EncryptedDetail(
        file = DataFactory.randomFile(),
        title = DataFactory.randomString(),
        uuid = DataFactory.randomUUID(),
        type = DataFactory.randomString(),
        dateTime = DataFactory.randomString(),
        thumbnail = DataFactory.randomString(),
        youtubeVideoID = DataFactory.randomString()
    )
}
