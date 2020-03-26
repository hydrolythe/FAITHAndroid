package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.Detail
import java.util.UUID

abstract class DetailsContainer {

    protected val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    open fun getLastDetail(): Detail {
        return _details.last()
    }

    open fun getDetail(uuid: UUID): Detail? {
        return details.find { it.uuid == uuid }
    }

    open fun addDetail(detail: Detail) {
        _details += detail
    }

    open fun removeDetail(detail: Detail) {
        _details.remove(detail)
    }
}