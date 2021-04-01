package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.faith.models.detail.Detail
import kotlin.reflect.KClass

class DetailTypeFilter<T : Detail>(
    private val detailType: KClass<T>
) : DetailFilter {

    override fun invoke(detail: Detail): Boolean {
        return detail::class == detailType
    }
}