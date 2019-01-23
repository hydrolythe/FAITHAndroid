package be.hogent.faith.service.usecases.base

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

abstract class CompleteableUseCase(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {
    private val disposables = CompositeDisposable()

    protected abstract fun buildUseCaseObservable(): Completable

    open fun execute(singleObserver: DisposableCompletableObserver) {
        val completable = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler)
        addDisposable(completable.subscribeWith(singleObserver))
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
