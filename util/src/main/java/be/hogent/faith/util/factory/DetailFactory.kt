package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType

object DetailFactory {

    fun makeDetail(): Detail {
        return Detail(
            DetailType.values().random(),
            DataFactory.randomFile(),
            DataFactory.randomUUID()
        )
    }
}