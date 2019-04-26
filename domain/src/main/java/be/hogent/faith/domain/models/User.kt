package be.hogent.faith.domain.models

import java.util.UUID

data class User(
    val username: String,
    // the resource entry name
    // TODO: find out how to work with avatar, especially persisting it in DB
//    val avatar: Avatar?,
    val uuid: UUID = UUID.randomUUID()
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
