package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event
import org.threeten.bp.LocalDate

class EventDateFilter(
    var startDate: LocalDate,
    var endDate: LocalDate
) : EventFilter {

    override fun invoke(event: Event): Boolean {
        with(event.dateTime) {
            return this.isAfter(startDate.atStartOfDay()) &&
                    this.isBefore(endDate.plusDays(1).atStartOfDay())
        }
    }
}