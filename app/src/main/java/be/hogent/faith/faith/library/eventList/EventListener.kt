package be.hogent.faith.faith.library.eventList

import java.util.UUID

interface EventListener {
    fun onEventClicked(eventUUID: UUID)
}
