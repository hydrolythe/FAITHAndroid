package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import kotlin.reflect.KClass

class EventHasDetailTypeFilter<T : Detail>(
    private val detailType: KClass<T>
) : (Event) -> Boolean {

    override fun invoke(p1: Event): Boolean {
        return p1.details.any { it::class == detailType }
    }

}