package be.hogent.faith.domain

class Timeline {

    private val _events = mutableListOf<Event>()
    val events: List<Event>
        get() = _events

    fun addEvent(event: Event) {
        _events += event
    }
}