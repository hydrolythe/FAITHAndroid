package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveBackpackTextDetailUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackTextDetailUseCase.Params>(
    observeScheduler
) {
    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params

        return addDetailToBackpack(params.textDetail)
        //    .flatMap { storageRepository.saveBackpackDetail(it) }
            .flatMapMaybe { backpackRepository.insertDetail(params.textDetail, params.user) }
            .flatMapCompletable { Completable.complete() }
    }

    private fun addDetailToBackpack(detail: TextDetail) : Single<Detail> = Single.fromCallable {
        params!!.user.backpack.addDetail(detail)
        detail
    }

    data class Params(val user : User, val textDetail: TextDetail)
}
