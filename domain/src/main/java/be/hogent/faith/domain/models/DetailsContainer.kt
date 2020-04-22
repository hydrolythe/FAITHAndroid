package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.Detail
import java.util.UUID

/**
 * Represents a container of details (duh).
 * The backpack, cinema,... all share the functionality of displaying a collection of details.
 *
 * The event is different from the DetailsContainer because there are multiple events, but there
 * is always only one Backpack, Cinema,... This has consequences from the service layer and beyond,
 * where for example encryption and storage has to be handled differently.
 */
sealed class DetailsContainer {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun get(uuid: UUID): Detail? {
        return details.find { it.uuid == uuid }
    }

    fun addDetail(detail: Detail) {
        _details += detail
    }

    fun removeDetail(detail: Detail) {
        _details.remove(detail)
    }
}

class Backpack : DetailsContainer()

class Cinema : DetailsContainer() {

    private val _films = mutableListOf<Film>()
    val films: List<Film>
        get() = _films

    fun getFilm(uuid: UUID): Film? {
        return films.find { it.uuid == uuid }
    }

    fun addFilm(film: Film) {
        // TODO : validatie toevoegen : duration - naam - datum
        _films += film
    }

    fun removeFilm(film: Film) {
        _films.remove(film)
    }
}
