package be.hogent.faith.service.usecases.base

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

/**
 * Base class for a use case that will return a [Single].
 *
 * Use the [Params] to define the input for the Use Case.
 * If there is more than one input for the Use Case, the subclass of this [MaybeUseCase] should also define
 * a class that has all the required inputs as public attributes, and define that as the [Params].
 * An example can be found in [be.hogent.faith.service.usecases.SaveEventUseCase].
 */
abstract class MaybeUseCase<Result, in Params>(
    private val observer: Scheduler
) {
    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseMaybe(params: Params): Maybe<Result>

    open fun execute(params: Params, singleObserver: DisposableMaybeObserver<Result>) {
        val single = this.buildUseCaseMaybe(params)
            .subscribeOn(Schedulers.io())
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
