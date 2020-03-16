package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.service.usecases.base.FlowableUseCase
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.backpack.DummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler

class GetBackPackFilesDummyUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
    ) : FlowableUseCase<List<Detail>, GetBackPackFilesDummyUseCase.Params>(observeScheduler)
{
    override fun buildUseCaseObservable(params: Params): Flowable<List<Detail>> {
        return backpackRepository.get()
    }

    data class Params(val backpack : String)
}