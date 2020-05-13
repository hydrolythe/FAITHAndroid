package be.hogent.faith.service.usecases.user

import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class LogoutUserUseCase(
    private val authManager: IAuthManager,
    observer: Scheduler
) : CompletableUseCase<Void?>(observer) {

    override fun buildUseCaseObservable(params: Void?): Completable {
        return authManager.signOut()
    }
}
