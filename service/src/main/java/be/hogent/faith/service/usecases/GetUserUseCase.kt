package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class GetUserUseCase(
    private val userRepository: UserRepository,
    observeScheduler: Scheduler
) : FlowableUseCase<User, GetUserUseCase.Params>(Schedulers.io(), observeScheduler) {
    override fun buildUseCaseObservable(params: GetUserUseCase.Params?): Flowable<User> {
        if (params == null) throw IllegalArgumentException("Params can't be null!")
        return userRepository.get(params.userUuid)
    }

    data class Params(
        val userUuid: UUID
    )
}
