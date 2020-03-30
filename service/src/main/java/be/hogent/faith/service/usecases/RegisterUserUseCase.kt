package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.IAuthManager
import be.hogent.faith.domain.repository.IUserRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class RegisterUserUseCase(
    private val authManager: IAuthManager,
    private val userRepository: IUserRepository,
    observer: Scheduler
) : CompletableUseCase<RegisterUserUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return authManager.register("${params.username}@faith.be", params.password)
            .flatMapCompletable { userRepository.insert(User(params.username, params.avatar, it)) }
    }

    data class Params(
        val username: String,
        val password: String,
        val avatar: String
    )
}