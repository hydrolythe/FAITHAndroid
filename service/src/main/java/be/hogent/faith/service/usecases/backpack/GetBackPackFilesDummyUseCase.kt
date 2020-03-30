package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.IBackpackRepository
import be.hogent.faith.service.usecases.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetBackPackFilesDummyUseCase(
    private val backpackRepository: IBackpackRepository,
    observeScheduler: Scheduler
) : ObservableUseCase<List<Detail>, GetBackPackFilesDummyUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Observable<List<Detail>> {
        return backpackRepository.get()
    }

    data class Params(val backpack: String)
}