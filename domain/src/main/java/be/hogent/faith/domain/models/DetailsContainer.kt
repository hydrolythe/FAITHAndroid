package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.Detail
import java.util.UUID

sealed class DetailsContainer {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun get(uuid: UUID): Detail? {
        return details.find { it.uuid == uuid }
    }

    fun addDetail(detail: Detail) {
        _details += detail
    }

    fun removeDetail(detail: Detail) {
        _details.remove(detail)
    }
}

class Backpack : DetailsContainer()
