package be.hogent.faith.faith.library.eventList

import be.hogent.faith.domain.models.Event
import java.util.UUID

interface EventListener {
    fun onEventClicked(eventUUID: UUID)
    fun onEventDeleteClicked(event: Event)
}
