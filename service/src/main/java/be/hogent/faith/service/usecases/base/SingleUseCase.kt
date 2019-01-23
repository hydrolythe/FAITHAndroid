package be.hogent.faith.service.usecases.base

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

abstract class SingleUseCase<Result, in Params>(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {
    private val disposables = CompositeDisposable()

    protected abstract fun buildUseCaseObservable(params: Params): Single<Result>

    open fun execute(params: Params): Single<Result>{
        return this.buildUseCaseObservable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler) as Single<Result>
    }


}