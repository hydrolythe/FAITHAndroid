package be.hogent.faith.service.usecases.base

import io.reactivex.Flowable
import io.reactivex.Scheduler

abstract class FlowableUseCase<Result, in Params>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler
) {

    protected abstract fun buildUseCaseObservable(params: Params? = null): Flowable<Result>

    open fun execute(params: Params?): Flowable<Result> {
        return this.buildUseCaseObservable(params)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler) as Flowable<Result>
    }
}
