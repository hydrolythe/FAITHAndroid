package be.hogent.faith.database.models

import org.threeten.bp.LocalDate

class Event(val date: LocalDate,
            val description: String,
            val category: Category,
            val details: List<Detail>)