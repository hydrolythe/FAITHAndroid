package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

class DeleteGoalUseCase(
    private val goalRepository: IGoalRepository,
    observer: Scheduler
) : CompletableUseCase<DeleteGoalUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return goalRepository.delete(params.goal.uuid)
            .andThen(Completable.fromAction { params.user.removeGoal(params.goal) })
    }

    data class Params(val goal: Goal, val user: User)
}
