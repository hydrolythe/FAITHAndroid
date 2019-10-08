package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import java.lang.RuntimeException
import java.util.UUID

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val authManager: AuthManager,
    observeScheduler: Scheduler
) : FlowableUseCase<User,Void?>(observeScheduler) {

    override fun buildUseCaseObservable(params: Void? ): Flowable<User> {
        val currentUser = authManager.getLoggedInUser()
        if (currentUser == null)
            throw RuntimeException("You are not allowed to access teh user, please log in");
        return userRepository.get(currentUser)
    }

}
