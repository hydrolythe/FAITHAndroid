package be.hogent.faith.service.usecases.user

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Flowables
import io.reactivex.schedulers.Schedulers

class GetUserUseCase(
    private val userRepository: IUserRepository,
    private val eventRepository: IEventRepository,
    private val eventEncryptionService: IEventEncryptionService,
    private val authManager: IAuthManager,
    observeScheduler: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : FlowableUseCase<User, GetUserUseCase.Params>(observeScheduler, subscriber) {

    override fun buildUseCaseObservable(params: Params): Flowable<User> {
        val currentUser = authManager.getLoggedInUserUUID()
        if (currentUser == null) {
            return Flowable.error(RuntimeException("You are not allowed to access the user, please log in"))
        }
        return Flowables.combineLatest(
            userRepository.get(currentUser)
                .subscribeOn(subscriber),
            eventRepository.getAll()
                .subscribeOn(subscriber)
                .concatMapSingle { list ->
                    Observable.fromIterable(list)
                        .flatMapSingle {
                            eventEncryptionService.decryptData(it).subscribeOn(subscriber)
                        }
                        .toList()
                }

        )
            .map { pair: Pair<User, List<Event>> -> addEventsToUser(pair.first, pair.second) }
    }

    private fun addEventsToUser(user: User, events: List<Event>): User {
        user.clearEvents()
        events.forEach { user.addEvent(it) }
        return user
    }

    class Params
}
