package be.hogent.faith.service.usecases.interfaces


import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class IsUsernameUniqueUseCase(
    private val userRepository: UserRepository,
    observer: Scheduler
) : SingleUseCase<Boolean, IsUsernameUniqueUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<Boolean> {
        if (params.username.isNullOrBlank())
            return Single.error(RuntimeException("username moet ingevuld zijn"))
       return userRepository.isUsernameUnique(params.username)
    }

    data class Params(
        val username: String
    )
}