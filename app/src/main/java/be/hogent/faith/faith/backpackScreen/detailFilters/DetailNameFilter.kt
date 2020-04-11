package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.Detail
import java.util.Locale

class DetailNameFilter(var searchString: String) : DetailFilter {

    override fun invoke(detail: Detail): Boolean {
        val searchWords = searchString.split(" ")
        return searchWords.all {
            detail.title.toLowerCase(Locale.ROOT).contains(it.toLowerCase(Locale.ROOT))
        }
    }
}