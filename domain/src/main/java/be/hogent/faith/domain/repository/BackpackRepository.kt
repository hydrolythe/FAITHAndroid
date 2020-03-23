package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface BackpackRepository {
    fun insertDetail(detail: Detail, user: User): Maybe<Detail>

    fun get(): Flowable<List<Detail>>

    fun deleteDetail(detail: Detail): Completable
}