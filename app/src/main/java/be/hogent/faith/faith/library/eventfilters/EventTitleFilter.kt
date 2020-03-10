package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event

class EventTitleFilter(var searchString: String) : EventFilter {
    override fun invoke(p1: Event): Boolean {
        return p1.title?.contains(searchString) ?: false
    }
}