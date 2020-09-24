package be.hogent.faith.service.usecases.user

import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.usecases.base.MaybeUseCase
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler

class LoginUserUseCase(
    private val authManager: IAuthManager,
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