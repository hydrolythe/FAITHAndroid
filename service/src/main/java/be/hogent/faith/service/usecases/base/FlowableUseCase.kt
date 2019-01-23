package be.hogent.faith.service.usecases.base

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.Executor

abstract class FlowableUseCase<T>(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {
    private val disposables = CompositeDisposable()

    protected abstract fun buildUseCaseObservable(): Flowable<T>

    open fun execute(observer: DisposableSubscriber<T>) {
        val flowable = this.buildUseCaseObservable()
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler) as Flowable<T>
        addDisposable(flowable.subscribeWith(observer))
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
