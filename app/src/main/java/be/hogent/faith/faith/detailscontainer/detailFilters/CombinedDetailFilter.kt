package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.detail.VideoDetail
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import org.threeten.bp.LocalDate

class CombinedDetailFilter {
    val titleFilter = DetailNameFilter("")
    val dateFilter = DetailDateFilter(LocalDate.MIN.plusDays(1), LocalDate.MAX.minusDays(1))

    val hasTextDetailFilter = ToggleableDetailFilter(DetailTypeFilter(TextDetail::class))
    val hasPhotoDetailFilter = ToggleableDetailFilter(DetailTypeFilter(PhotoDetail::class))
    val hasAudioDetailFilter = ToggleableDetailFilter(DetailTypeFilter(AudioDetail::class))
    val hasDrawingDetailFilter = ToggleableDetailFilter(DetailTypeFilter(DrawingDetail::class))
    val hasVideoDetailFilter =
        ToggleableDetailFilter(DetailTypeFilter(VideoDetail::class))
    val hasYoutubeDetailFilter = ToggleableDetailFilter(DetailTypeFilter(YoutubeVideoDetail::class))

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
        if (hasVideoDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasVideoDetailFilter))
        }
        if (hasYoutubeDetailFilter.isEnabled) {
            filteredDetails.addAll(details.filter(hasYoutubeDetailFilter))
        }

        if (filteredDetails.isEmpty() && !isDetailTypeFilterActive()) {
            return details.filter(titleFilter).filter(dateFilter)
        }
        return filteredDetails.filter(titleFilter).filter(dateFilter)
    }

    private fun isDetailTypeFilterActive(): Boolean {
        return hasVideoDetailFilter.isEnabled || hasPhotoDetailFilter.isEnabled || hasDrawingDetailFilter.isEnabled || hasTextDetailFilter.isEnabled || hasAudioDetailFilter.isEnabled || hasYoutubeDetailFilter.isEnabled
    }
}
