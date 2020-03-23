package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveBackpackExternalVideoDetailUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackExternalVideoDetailUseCase.Params>(
        observeScheduler
) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params

        return addDetailToBackpack(params.externalVideoDetail)
                .flatMapMaybe { backpackRepository.insertDetail(params.externalVideoDetail, params.user) }
                .flatMapCompletable { Completable.complete() }
    }

    private fun addDetailToBackpack(externalVideoDetail: ExternalVideoDetail): Single<Detail> = Single.fromCallable {
        params!!.user.backpack.addDetail(externalVideoDetail)
        externalVideoDetail
    }

    data class Params(val user: User, val externalVideoDetail: ExternalVideoDetail)
}