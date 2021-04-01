package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.faith.models.Event
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import org.threeten.bp.LocalDate

class CombinedEventFilter {
    val dateFilter = EventDateFilter(LocalDate.MIN.plusDays(1), LocalDate.MAX.minusDays(1))

    val titleFilter = EventTitleFilter("")

    val hasTextDetailFilter = ToggleableEventFilter(EventHasDetailTypeFilter(TextDetail::class))
    val hasPhotoDetailFilter = ToggleableEventFilter(EventHasDetailTypeFilter(PhotoDetail::class))
    val hasAudioDetailFilter = ToggleableEventFilter(EventHasDetailTypeFilter(AudioDetail::class))
    val hasDrawingDetailFilter =
        ToggleableEventFilter(EventHasDetailTypeFilter(DrawingDetail::class))

    fun filter(events: List<Event>): List<Event> {
        val combined = events.asSequence()
            .filter(titleFilter)
            .filter(dateFilter)
            .filter(hasTextDetailFilter)
            .filter(hasPhotoDetailFilter)
            .filter(hasAudioDetailFilter)
            .filter(hasDrawingDetailFilter)
            .toList()
        return combined
    }
}