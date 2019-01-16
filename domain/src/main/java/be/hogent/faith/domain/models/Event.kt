package be.hogent.faith.domain.models

import org.threeten.bp.LocalDate

class Event(
    val date: LocalDate,
    val description: String
) {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun addDetail(detail: Detail) {
        _details += detail
    }
}