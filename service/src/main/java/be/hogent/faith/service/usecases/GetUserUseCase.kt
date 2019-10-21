package be.hogent.faith.service.usecases

import android.content.ContentValues.TAG
import android.util.Log
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Flowables.combineLatest
import java.lang.RuntimeException

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val authManager: AuthManager,
    observeScheduler: Scheduler
) : FlowableUseCase<User, Void?>(observeScheduler) {

    override fun buildUseCaseObservable(params: Void?): Flowable<User> {
        val currentUser = authManager.getLoggedInUser()
        if (currentUser == null)
            return Flowable.error(RuntimeException("You are not allowed to access the user, please log in"))
        return combineLatest(userRepository.get(currentUser), eventRepository.getAll())
            .map { pair: Pair<User, List<Event>> -> addEventsToUser(pair.first, pair.second) }
            .doOnNext { Log.d(TAG, "user ${it.username} heeft ${it.events.count()} events") }
    }
}

private fun addEventsToUser(user: User, events: List<Event>): User {
    user.clearEvents()
    events.forEach { user.addEvent(it) }
    return user
}
