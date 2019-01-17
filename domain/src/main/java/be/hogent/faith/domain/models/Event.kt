package be.hogent.faith.domain.models

import org.threeten.bp.LocalDateTime
import java.util.UUID

data class Event(
    val date: LocalDateTime,
    val description: String,
    val uuid: UUID = UUID.randomUUID()
) {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun addDetail(detail: Detail) {
        _details += detail
    }
}