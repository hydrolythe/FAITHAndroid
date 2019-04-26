package be.hogent.faith.service.usecases.base

import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Base class for a use case that will return a [Completable].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [CompletableUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class CompletableUseCase<in Params>(
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler
) {

    /**
     * This should be overridden with the business logic for the use case.
     */
    abstract fun buildUseCaseObservable(params: Params): Completable

    /**
     * Executes the use case.
     * It will run on the specified [subscribeScheduler] and can be observed on the given [observeScheduler].
     */
    open fun execute(params: Params): Completable {
        return this.buildUseCaseObservable(params)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}
