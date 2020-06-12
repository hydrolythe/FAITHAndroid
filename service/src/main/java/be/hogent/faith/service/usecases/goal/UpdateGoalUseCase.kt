package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber

class UpdateGoalUseCase(
    private val goalEncryptionService: IGoalEncryptionService,
    private val goalRepository: IGoalRepository,
    observer: Scheduler
) : CompletableUseCase<UpdateGoalUseCase.Params>(observer) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        return goalEncryptionService.encrypt(params.goal)
            .doOnSuccess { Timber.i("encrypted goal ${params.goal.uuid}") }
            .flatMapCompletable(goalRepository::update)
            .doOnComplete { Timber.i("updated the goal ${params.goal.uuid}") }
            .doOnError { Timber.e("Error while updating goal ${params.goal.uuid}: ${it.localizedMessage}") }
    }

    data class Params(
        var goal: Goal
    )
}