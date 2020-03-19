package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface BackpackRepository{
    fun insertDetail(detail: Detail, user: User): Maybe<Detail>

    fun get(): Flowable<List<Detail>>
}