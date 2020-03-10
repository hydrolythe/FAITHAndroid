package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event

interface EventFilter : (Event) -> Boolean

