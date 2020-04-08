package be.hogent.faith.domain.models

import java.util.UUID
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