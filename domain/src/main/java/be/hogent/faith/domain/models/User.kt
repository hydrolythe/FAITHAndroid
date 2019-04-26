package be.hogent.faith.domain.models

import java.util.UUID

data class User(
    val uuid: UUID = UUID.randomUUID(),
    val avatar: Avatar? = null,
    val username: String
) {
    private val _events = HashMap<UUID, Event>()
    val events: List<Event>
        get() = _events.values.toList()

    fun addEvent(event: Event) {
        if (event.title.isNullOrBlank()) {
            throw IllegalArgumentException("Een gebeurtenis moet een ingevulde titel hebben.")
        }
        _events[event.uuid] = event
    }

    fun getEvent(eventUUID: UUID): Event? {
        return _events[eventUUID]
    }
}
