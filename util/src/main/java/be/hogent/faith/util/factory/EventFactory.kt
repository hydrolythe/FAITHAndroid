package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.Event

object EventFactory {

    fun makeEvent(nbrOfDetails: Int = 5, hasEmotionAvatar: Boolean = false): Event {
        val event = Event(
            DataFactory.randomDateTime(),
            DataFactory.randomString(),
            if (hasEmotionAvatar) DataFactory.randomFile() else null,
            DataFactory.randomUUID()
        )
        repeat(nbrOfDetails) {
            event.addDetail(DetailFactory.makeDetail())
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
