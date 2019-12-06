package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface DetailRepository {
    fun delete(detail: Detail): Completable

    fun insert(detail: Detail): Completable

    fun get(uuid: UUID): Single<Detail>
}
