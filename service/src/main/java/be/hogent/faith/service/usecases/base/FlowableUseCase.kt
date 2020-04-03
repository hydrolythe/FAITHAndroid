package be.hogent.faith.service.usecases.base

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

/**
 * Base class for a use case that will return a [Flowable].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [FlowableUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class FlowableUseCase<Result, in Params>(
    private val observer: Scheduler
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
            .subscribeOn(Schedulers.io())
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
