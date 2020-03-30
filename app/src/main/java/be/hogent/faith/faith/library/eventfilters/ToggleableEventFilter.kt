package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event

class ToggleableEventFilter(val filter: EventFilter) : EventFilter {

    var isEnabled = false

    override fun invoke(event: Event): Boolean {
        return !isEnabled || filter.invoke(event)
    }

    fun toggle() {
        isEnabled = !isEnabled
    }
}