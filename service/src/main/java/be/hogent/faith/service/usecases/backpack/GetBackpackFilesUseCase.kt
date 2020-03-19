/*
package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.toFlowable

class GetBackpackFilesUseCase(
    private val storageRepository : IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<GetBackpackFilesUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return storageRepository.getBackpack(params.details.toFlowable())
    }

    data class Params(val details : List<Detail>)
}*/
