package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

open class AddNewGoalUseCase(
    private val goalEncryptionService: IGoalEncryptionService,
    private val goalRepository: IGoalRepository,
    observer: Scheduler
) : CompletableUseCase<AddNewGoalUseCase.Params>(observer) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return Single.just(params.user)
            .map { user -> user.addNewGoal() }
            .flatMap { newGoal -> goalEncryptionService.encrypt(newGoal) }
            .doOnSuccess { Timber.i("encrypted new goal") }
            .flatMapCompletable(goalRepository::update)
            .doOnComplete { Timber.i("stored data for new goal") }
    }

    data class Params(
        val user: User
    )
}
