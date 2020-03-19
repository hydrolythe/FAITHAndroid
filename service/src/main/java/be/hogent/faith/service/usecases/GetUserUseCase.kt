package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Flowables.combineLatest

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val eventRepository: IEventRepository,
    private val authManager: AuthManager,
    observeScheduler: Scheduler
) : FlowableUseCase<User, GetUserUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Flowable<User> {
        val currentUser = authManager.getLoggedInUserUUID()
        if (currentUser == null)
            return Flowable.error(RuntimeException("You are not allowed to access the user, please log in"))
        return combineLatest(userRepository.get(currentUser), eventRepository.getAllEventsData())
            .map { pair: Pair<User, List<Event>> -> addEventsToUser(pair.first, pair.second) }
    }

    class Params

    private fun addEventsToUser(user: User, events: List<Event>): User {
        user.clearEvents()
        events.forEach { user.addEvent(it) }
        return user
    }
}
