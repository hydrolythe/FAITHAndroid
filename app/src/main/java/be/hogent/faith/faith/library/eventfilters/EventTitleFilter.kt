package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.faith.models.Event

class EventTitleFilter(var searchString: String) : EventFilter {

    override fun invoke(event: Event): Boolean {
        val searchWords = searchString.split(" ")
        return searchWords.all {
            event.title?.contains(it) ?: false
        }
    }
}