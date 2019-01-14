package be.hogent.faith.domain

import org.threeten.bp.LocalDate

class Event(
    val date: LocalDate,
    val description: String,
    val category: Category
) {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun addDetail(detail: Detail) {
        _details += detail
    }
}