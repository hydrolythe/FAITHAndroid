package be.hogent.faith.domain.models

import java.util.UUID

data class User(
    val username: String,
    // the resource entry name
    val avatar: String,
    val uuid: UUID = UUID.randomUUID()
) {
    private val _events = mutableListOf<Event>()
    val events: List<Event>
        get() = _events

    fun addEvent(event: Event) {
        _events += event
    }

    fun getEvent(eventUUID: UUID): Event? {
        return _events.find { it.uuid == eventUUID }
    }
}
