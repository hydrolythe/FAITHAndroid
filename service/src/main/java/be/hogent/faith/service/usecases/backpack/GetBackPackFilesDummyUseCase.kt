package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler

class GetBackPackFilesDummyUseCase(
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    observeScheduler: Scheduler
) : FlowableUseCase<List<Detail>, GetBackPackFilesDummyUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Flowable<List<Detail>> {
        return backpackRepository.get()
    }

    data class Params(val backpack: String)
}