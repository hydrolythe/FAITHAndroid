package be.hogent.faith.service.usecases.base

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Base class for a use case that will return a [Single].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [SingleUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class SingleUseCase<Result, in Params>(
    private val observer: Scheduler,
    protected val subscriber: Scheduler = Schedulers.io()
) {
    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseSingle(params: Params): Single<Result>

    open fun execute(params: Params, singleObserver: DisposableSingleObserver<Result>) {
        val single = this.buildUseCaseSingle(params)
            .subscribeOn(subscriber)
            .observeOn(observer)
        addDisposable(single.subscribeWith(singleObserver))
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}
