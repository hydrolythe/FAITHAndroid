package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.Detail

class CombinedDetailFilter {
    val titleFilter = DetailNameFilter("")

    val hasTextDetailFilter = ToggleableDetailFilter(DetailTypeFilter(TextDetail::class))
    val hasPhotoDetailFilter = ToggleableDetailFilter(DetailTypeFilter(PhotoDetail::class))
    val hasAudioDetailFilter = ToggleableDetailFilter(DetailTypeFilter(AudioDetail::class))
    val hasDrawingDetailFilter = ToggleableDetailFilter(DetailTypeFilter(DrawingDetail::class))

    fun filter(details: List<Detail>): List<Detail> {
        val filteredDetails = mutableListOf<Detail>()
        if (hasTextDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasTextDetailFilter))
        }
        if (hasAudioDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasAudioDetailFilter))
        }
        if (hasDrawingDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasDrawingDetailFilter))
        }
        if (hasPhotoDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasPhotoDetailFilter))
        }

        if (filteredDetails.isEmpty()) {
            return details.filter(titleFilter)
        }
        return filteredDetails.filter(titleFilter)
    }
}
