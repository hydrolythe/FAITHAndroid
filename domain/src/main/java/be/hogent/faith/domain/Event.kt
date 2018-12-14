package be.hogent.faith.domain

import org.threeten.bp.LocalDate

class Event(
    val date: LocalDate,
    val description: String,
    val category: Category
) {

    val details = mutableListOf<Detail>()
}