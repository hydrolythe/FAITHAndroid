package be.hogent.faith.service.usecases.base

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Base class for a use case that will return a [Completable].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [CompletableUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class CompletableUseCase<in Params>(
    private val observer: Scheduler,
    protected val subscriber: Scheduler = Schedulers.io()
) {

    private val disposables = CompositeDisposable()

    /**
     * This should be overridden with the business logic for the use case.
     */
    abstract fun buildUseCaseObservable(params: Params): Completable

    /**
     * Executes the use case.
     * It will run on the specified [subscribeScheduler] and can be observed on the given [observer].
     */
    open fun execute(params: Params, completableObserver: DisposableCompletableObserver) {
        val completable = this.buildUseCaseObservable(params)
            .subscribeOn(subscriber)
            .observeOn(observer)
        addDisposable(completable.subscribeWith(completableObserver))
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}