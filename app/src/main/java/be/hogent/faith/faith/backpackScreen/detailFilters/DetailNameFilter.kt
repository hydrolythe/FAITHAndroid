package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.Detail
import java.util.*

class DetailNameFilter(var searchString: String) : DetailFilter {

    override fun invoke(detail: Detail): Boolean {
        val searchWords = searchString.split(" ")
        return searchWords.all {
            detail.fileName.toLowerCase(Locale.ROOT).contains(it.toLowerCase(Locale.ROOT))
        }
    }
}