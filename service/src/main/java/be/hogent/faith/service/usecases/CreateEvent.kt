package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class CreateEvent(
    @Inject
    private val eventRepository: Repository<Event>
) {

    fun execute(dateTime: LocalDateTime, description: String) {
        eventRepository.add(Event(dateTime, description))
    }
}