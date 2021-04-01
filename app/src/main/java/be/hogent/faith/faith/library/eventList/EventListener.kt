package be.hogent.faith.faith.library.eventList

import be.hogent.faith.faith.models.Event
import java.util.UUID

interface EventListener {
    fun onEventClicked(eventUUID: UUID)
    fun onEventDeleteClicked(event: Event)
}
