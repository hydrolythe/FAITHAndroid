package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.MaybeUseCase
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.UUID

class RegisterUserUseCase(
    private val authManager: AuthManager,
    observer: Scheduler
) : MaybeUseCase<String, RegisterUserUseCase.Params>(observer) {

    override fun buildUseCaseMaybe(params: Params): Maybe<String> {
        val email = "${params.username}@faith.be"
        return authManager.isUnique(email).flatMapMaybe {
            if (it)
                authManager.register(email, params.password)
            else
                Maybe.empty()
        }
    }


    data class Params(
        val username: String,
        val password: String,
        val uuid:UUID? = null
    )
}