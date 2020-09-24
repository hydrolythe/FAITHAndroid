package be.hogent.faith.service.usecases.util

import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.util.factory.DataFactory

object EncryptedEventFactory {

    fun makeEvent(numberOfDetails: Int = 5, hasEmotionAvatar: Boolean = false): EncryptedEvent {
        val event = EncryptedEvent(
            dateTime = DataFactory.randomDateTime().toString(),
            title = DataFactory.randomString(),
            emotionAvatar = if (hasEmotionAvatar) DataFactory.randomFile() else null,
            emotionAvatarThumbnail = DataFactory.randomString(),
            notes = DataFactory.randomString(),
            uuid = DataFactory.randomUUID(),
            encryptedStreamingDEK = DataFactory.randomString(),
            encryptedDEK = DataFactory.randomString()
        )
        val details = mutableListOf<EncryptedDetail>()
        repeat(numberOfDetails) {
            details.add(EncryptedDetailFactory.makeRandomDetail())
        }
        event.details = details
        return event
    }

    fun makeEventList(count: Int = 5): List<EncryptedEvent> {
        val events = mutableListOf<EncryptedEvent>()
        repeat(count) {
            events.add(makeEvent())
        }
        return events
    }
}
