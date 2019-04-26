package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class CreateUserUseCase(
    private val userRepository: UserRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<CreateUserUseCase.Params>(Schedulers.io(), observeScheduler) {

    override fun buildUseCaseObservable(params: CreateUserUseCase.Params): Completable {
        val user = User(params.username, params.avatar)
        return userRepository.insert(user)
    }

    data class Params(
        val username: String,
        val avatar: String
    )
}