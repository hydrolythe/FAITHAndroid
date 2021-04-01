package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.faith.models.detail.Detail

class ToggleableDetailFilter(val filter: DetailFilter) : DetailFilter {

    var isEnabled = false

    override fun invoke(detail: Detail): Boolean {
        return !isEnabled || filter.invoke(detail)
    }
}
