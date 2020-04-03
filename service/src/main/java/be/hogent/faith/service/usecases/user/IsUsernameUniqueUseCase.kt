package be.hogent.faith.service.usecases.user

import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class IsUsernameUniqueUseCase(
    private val authManager: IAuthManager,
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