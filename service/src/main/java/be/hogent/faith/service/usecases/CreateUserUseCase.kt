package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CreateUserUseCase(
    private val userRepository: UserRepository,
    observeScheduler: Scheduler
) : SingleUseCase<User, CreateUserUseCase.Params>(Schedulers.io(), observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Single<User> {
//        val user = User(params.username, params.avatar)
        // TODO: add avatar again once available
        val user = User(params.username)
        return userRepository.insert(user).andThen(Single.just(user))
    }

    data class Params(
        val username: String,
        val avatar: String
    )
}