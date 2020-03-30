package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.IAuthManager
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class LogoutUserUseCase(
    private val authManager: IAuthManager,
    observeScheduler: Scheduler
) : CompletableUseCase<Void?>(observeScheduler) {

    override fun buildUseCaseObservable(params: Void?): Completable {
        return authManager.signOut()
    }
}
