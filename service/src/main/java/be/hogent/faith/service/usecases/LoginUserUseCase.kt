package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.service.usecases.base.MaybeUseCase
import io.reactivex.Maybe
import io.reactivex.Scheduler

class LoginUserUseCase(
    private val authManager: AuthManager,
    observer: Scheduler
) : MaybeUseCase<String?, LoginUserUseCase.Params>(observer) {

    override fun buildUseCaseMaybe(params: Params): Maybe<String?> {
        return authManager.signIn("${params.username}@faith.be", params.password)
    }

    data class Params(
        val username: String,
        val password: String
    )
}