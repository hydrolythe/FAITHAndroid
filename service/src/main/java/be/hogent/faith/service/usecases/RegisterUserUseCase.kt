package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class RegisterUserUseCase(
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    observer: Scheduler
) : CompletableUseCase<RegisterUserUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: RegisterUserUseCase.Params): Completable {
        // return Completable.fromCallable {
        return authManager.register("${params.username}@faith.be", params.password)
            .flatMapCompletable { userRepository.insert(User(params.username, params.avatar, it)) }
    }

    data class Params(
        val username: String,
        val password: String,
        val avatar: String
    )
}