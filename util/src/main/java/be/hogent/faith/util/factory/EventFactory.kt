package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail

object EventFactory {

    fun makeEvent(nbrOfDetails: Int = 5, hasEmotionAvatar: Boolean = false): Event {
        val event = Event(
            dateTime = DataFactory.randomDateTime(),
            title = DataFactory.randomString(),
            emotionAvatar = if (hasEmotionAvatar) DataFactory.randomFile() else null,
            notes = DataFactory.randomString(),
            uuid = DataFactory.randomUUID()
        )
        repeat(nbrOfDetails) {
            DetailFactory.makeRandomDetail().let { detail ->
                when (detail) {
                    is AudioDetail -> event.addNewAudioDetail(detail.file, detail.name!!)
                    is DrawingDetail -> event.addNewDrawingDetail(detail.file, detail.name!!)
                    is TextDetail -> event.addNewTextDetail(detail.file, detail.name!!)
                    else -> event.addNewAudioDetail(detail.file, detail.name!!)
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
