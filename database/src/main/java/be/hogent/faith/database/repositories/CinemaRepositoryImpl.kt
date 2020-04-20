package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseCinemaRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.FilmMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.Film
import be.hogent.faith.domain.repository.CinemaRepository
import io.reactivex.Completable
import io.reactivex.Flowable

class CinemaRepositoryImpl(
    userMapper: UserMapper,
    detailMapper: DetailMapper,
    firebaseDetailContainerRepository: FirebaseCinemaRepository,
    private val filmMapper: FilmMapper
) : CinemaRepository, DetailContainerRepositoryImpl<Cinema>(
    userMapper,
    detailMapper,
    firebaseDetailContainerRepository
) {
    override fun getFilms(): Flowable<List<Film>> {
        return (firebaseDetailContainerRepository as FirebaseCinemaRepository).getFilms().map {
            filmMapper.mapFromEntities(it)
        }
    }

    override fun deleteFilm(film: Film): Completable {
        return (firebaseDetailContainerRepository as FirebaseCinemaRepository).deleteFilm(
            filmMapper.mapToEntity(
                film
            )
        )
    }
}