package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail

object EventFactory {

    fun makeEvent(numberOfDetails: Int = 5, hasEmotionAvatar: Boolean = false): Event {
        val event = Event(
            dateTime = DataFactory.randomDateTime(),
            title = DataFactory.randomString(),
            emotionAvatar = if (hasEmotionAvatar) DataFactory.randomFile() else null,
            notes = DataFactory.randomString(),
            uuid = DataFactory.randomUUID()
        )
        repeat(numberOfDetails) {
            DetailFactory.makeRandomDetail().let { detail ->
                when (detail) {
                    is AudioDetail -> event.addNewAudioDetail(detail.file)
                    is DrawingDetail -> event.addNewDrawingDetail(detail.file)
                    is TextDetail -> event.addNewTextDetail(detail.file)
                    else -> event.addNewAudioDetail(detail.file)
                }
            }
        }
        return event
    }

    fun makeEventList(count: Int = 5): List<Event> {
        val events = mutableListOf<Event>()
        repeat(count) {
            events.add(makeEvent())
        }
        return events
    }
}
