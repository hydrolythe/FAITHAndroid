package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber

open class SaveGoalUseCase(
    private val goalEncryptionService: IGoalEncryptionService,
    private val goalRepository: IGoalRepository,
    observer: Scheduler
) : CompletableUseCase<SaveGoalUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: SaveGoalUseCase.Params): Completable {
        return goalEncryptionService.encrypt(params.goal)
            .doOnSuccess { Timber.i("encrypted goal ${params.goal.uuid}") }
            .flatMapCompletable(goalRepository::insert)
            .doOnComplete { Timber.i("stored data for goal ${params.goal.uuid}") }
    }

    data class Params(
        var goal: Goal
    )
}
