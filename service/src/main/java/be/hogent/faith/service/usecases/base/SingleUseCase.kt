package be.hogent.faith.service.usecases.base

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

abstract class SingleUseCase<T>(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {
    private val disposables = CompositeDisposable()

    protected abstract fun buildUseCaseObservable(): Single<T>

    open fun execute(singleObserver: DisposableSingleObserver<T>) {
        val single = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler) as Single<T>
        addDisposable(single.subscribeWith(singleObserver))
    }

    fun dispose() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

}