package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.service.usecases.base.MaybeUseCase
import io.reactivex.Maybe
import io.reactivex.Scheduler

class RegisterUserUseCase(
    private val authManager: AuthManager,
    observer: Scheduler
) : MaybeUseCase<String?, RegisterUserUseCase.Params>(observer) {

    override fun buildUseCaseMaybe(params: Params): Maybe<String?> {
        return authManager.register("${params.username}@faith.be", params.password)
        /*    .concatMap{
                userRepository.insert(User(params.username, "geen avatar", it))}
            .andThen(Maybe.just(User(params.username, "geen avatar",uuid!! )))
    */ }

    data class Params(
        val username: String,
        val password: String
    )
}