package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

interface IBackpackRepository {
    fun insertDetail(detail: Detail, user: User): Maybe<Detail>

    fun get(): Observable<List<Detail>>

    fun deleteDetail(detail: Detail): Completable
}