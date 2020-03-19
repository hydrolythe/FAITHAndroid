package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.*

class CombinedDetailFilter {
    val titleFilter = DetailNameFilter("")

    val hasTextDetailFilter = ToggleableDetailFilter(DetailTypeFilter(TextDetail::class))
    val hasPhotoDetailFilter = ToggleableDetailFilter(DetailTypeFilter(PhotoDetail::class))
    val hasAudioDetailFilter = ToggleableDetailFilter(DetailTypeFilter(AudioDetail::class))
    val hasDrawingDetailFilter = ToggleableDetailFilter(DetailTypeFilter(DrawingDetail::class))

    fun filter(details: List<Detail>): List<Detail> {
        return details
                .asSequence()
                .filter(hasTextDetailFilter)
                .filter(hasPhotoDetailFilter)
                .filter(hasAudioDetailFilter)
                .filter(hasDrawingDetailFilter)
                .toList()
    }
}
