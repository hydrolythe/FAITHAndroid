package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.faith.models.Event

interface EventFilter : (Event) -> Boolean
