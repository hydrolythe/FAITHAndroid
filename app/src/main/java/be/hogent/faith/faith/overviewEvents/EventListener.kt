package be.hogent.faith.faith.overviewEvents

import java.util.UUID

interface EventListener {
    fun onEventClicked(eventUuid: UUID)
}
