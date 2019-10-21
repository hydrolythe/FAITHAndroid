package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class IsUsernameUniqueUseCase(
    private val authManager: AuthManager,
    observer: Scheduler
) : SingleUseCase<Boolean, IsUsernameUniqueUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<Boolean> {
        if (params.username.isNullOrBlank())
            return Single.error(RuntimeException("username moet ingevuld zijn"))
        return authManager.isUsernameUnique(params.username + "@faith.be")
    }

    data class Params(
        val username: String
    )
}