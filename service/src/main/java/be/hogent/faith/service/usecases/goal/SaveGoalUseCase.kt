package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber

open class SaveGoalUseCase(
    private val goalEncryptionService: IGoalEncryptionService,
    private val goalRepository: IGoalRepository,
    observer: Scheduler
) : CompletableUseCase<SaveGoalUseCase.Params>(observer) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return goalEncryptionService.encrypt(params.goal)
            .doOnSuccess { Timber.i("encrypted goal ${params.goal.uuid}") }
            .flatMapCompletable(goalRepository::insert)
            .doOnComplete { Timber.i("stored data for goal ${params.goal.uuid}") }
    }

    data class Params(
        var goal: Goal,
        val user: User
    )
}
