package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.Film

object FilmFactory {
    fun makeFilm(): Film {
        return Film(
            dateTime = DataFactory.randomDateTime(),
            title = DataFactory.randomString(),
            file = DataFactory.randomFile(),
            uuid = DataFactory.randomUUID()
        )
    }

    fun makeFilmList(count: Int = 5): List<Film> {
        val films = mutableListOf<Film>()
        repeat(count) {
            films.add(makeFilm())
        }
        return films
    }
}