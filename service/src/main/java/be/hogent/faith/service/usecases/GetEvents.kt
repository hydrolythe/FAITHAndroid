package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import io.reactivex.Flowable
import javax.inject.Inject

class GetEvents(
    @Inject
    private val eventRepository: Repository<Event>
) {
    fun execute(): Flowable<List<Event>> {
        return eventRepository.getAll()
    }
}