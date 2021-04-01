package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.faith.models.detail.Detail
import org.threeten.bp.LocalDate

class DetailDateFilter(
    var startDate: LocalDate,
    var endDate: LocalDate
) : DetailFilter {

    override fun invoke(detail: Detail): Boolean {
        with(detail.dateTime) {
            return this.isAfter(startDate.atStartOfDay()) &&
                    this.isBefore(endDate.plusDays(1).atStartOfDay())
        }
    }
}