package be.hogent.faith.service.usecases.base

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposables
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * Base class for a use case that will return a [Completable].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [CompleteableUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 */
abstract class CompleteableUseCase<in Params>(
    private val threadExecutor: Executor,
    private val scheduler: Scheduler
) {
    private val disposable = Disposables.empty()

    protected abstract fun buildUseCaseObservable(params: Params? = null): Completable

    open fun execute(params: Params?): Completable {
        return this.buildUseCaseObservable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(scheduler)
    }

    fun dispose() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

}
