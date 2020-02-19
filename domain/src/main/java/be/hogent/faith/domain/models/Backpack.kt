package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.Detail
import java.util.UUID

class Backpack(
    val uuid: UUID = UUID.randomUUID()
){
    private var _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details.toList()

    fun addDetail (detail : Detail) {
        _details.add(detail)
    }

    fun removeDetail (detail : Detail){
        _details.remove(detail)
    }

}