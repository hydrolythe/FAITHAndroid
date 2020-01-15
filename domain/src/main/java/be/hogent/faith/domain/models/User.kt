package be.hogent.faith.domain.models

import java.util.UUID

data class User(
    val username: String,
    /**
     * The name of the avatar this user chose.
     * This should be unique, and will be used to request the image corresponding to the chosen avatar.
     */
    val avatarName: String,
    val uuid: String
) {
    private var _events = HashMap<UUID, Event>()
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

    fun clearEvents() {
        _events = HashMap()
    }

    fun removeEvent(event: Event) {
        _events.remove(event.uuid)

    }
}
