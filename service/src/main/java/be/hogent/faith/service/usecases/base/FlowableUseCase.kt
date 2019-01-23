package be.hogent.faith.service.usecases.base

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

abstract class FlowableUseCase<Result, in Params>(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {

    protected abstract fun buildUseCaseObservable(params: Params? = null): Flowable<Result>

    open fun execute(params: Params?) : Flowable<Result> {
        return this.buildUseCaseObservable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler) as Flowable<Result>
    }

}
