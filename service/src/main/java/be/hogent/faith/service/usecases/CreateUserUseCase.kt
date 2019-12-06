package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateUserUseCase(
    private val userRepository: UserRepository,
    observer: Scheduler
) : SingleUseCase<User, CreateUserUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<User> {
        val user = User(params.username, params.avatarName, params.uuid)
        return userRepository.insert(user).andThen(Single.just(user))
    }

    data class Params(
        val username: String,
        val avatarName: String,
        val uuid: String
    )
}