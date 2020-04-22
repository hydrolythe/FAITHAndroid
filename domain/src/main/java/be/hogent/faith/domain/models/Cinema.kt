package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import java.util.UUID
class Cinema : DetailsContainer() {

    fun getFilm(uuid: UUID): FilmDetail? {
        return films.find { it.uuid == uuid }
    }

    val films: List<FilmDetail>
        get() = details.filterIsInstance<FilmDetail>()

    fun getFiles(): List<Detail> {
        return details.filter { it is FilmDetail }
    }

    fun addFilm(film: FilmDetail) {
        // TODO : validatie toevoegen : duration - naam - datum
        addDetail(film)
    }

    fun removeFilm(film: FilmDetail) {
        removeDetail(film)
    }
}