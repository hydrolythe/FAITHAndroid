package be.hogent.faith.service.usecases.base

import io.reactivex.Scheduler
import io.reactivex.Single

abstract class SingleUseCase<Result, in Params>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler
) {
    protected abstract fun buildUseCaseObservable(params: Params): Single<Result>

    open fun execute(params: Params): Single<Result> {
        return this.buildUseCaseObservable(params)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler) as Single<Result>
    }
}