package be.hogent.faith.service.usecases.base

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subscribers.DisposableSubscriber

/**
 * Base class for a use case that will return a [Flowable].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [FlowableUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class FlowableUseCase<Result, in Params>(
    private val observer: Scheduler,
    protected val subscriber: Scheduler = Schedulers.io()
) {

    private val disposables = CompositeDisposable()

    /**
     * This should be overridden with the business logic for the use case.
     */
    abstract fun buildUseCaseObservable(params: Params): Flowable<Result>

    /**
     * Executes the use case.
     * It will run on the specified [subscribeScheduler] and can be observed on the given [observeScheduler].
     */
    open fun execute(params: Params, flowableObserver: DisposableSubscriber<Result>) {
        val flowable = this.buildUseCaseObservable(params)
            .subscribeOn(subscriber)
            .observeOn(observer)
        addDisposable(flowable.subscribeWith(flowableObserver))
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}
