package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.Detail
import kotlin.reflect.KClass

class DetailTypeFilter<T : Detail>(
        private val detailType: KClass<T>
) : DetailFilter {

    override fun invoke(detail: Detail): Boolean {
        return detail::class == detailType
    }
}