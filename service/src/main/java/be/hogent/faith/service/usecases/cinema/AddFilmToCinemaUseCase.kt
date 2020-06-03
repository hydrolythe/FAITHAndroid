package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class AddFilmToCinemaUseCase(
    private val containerEncryptionService: IDetailContainerEncryptionService<Cinema>,
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val fileStorageRepository: IFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<AddFilmToCinemaUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        // Encrypt film
        return cinemaRepository.getEncryptedContainer()
            .subscribeOn(subscriber)
            .flatMap { encryptedCinema ->
                containerEncryptionService.encrypt(params.film, encryptedCinema)
                    .subscribeOn(subscriber)
            }
            .flatMap { encryptedMovie ->
                fileStorageRepository.saveDetailFileWithContainer(encryptedMovie, params.cinema)
                    .subscribeOn(subscriber)
            }
            .flatMapCompletable { encryptedMovie ->
                cinemaRepository.insertDetail(
                    encryptedMovie,
                    params.user
                ).subscribeOn(subscriber)
            }.andThen {
                Completable.fromAction {
                    params.cinema.addFilm(params.film)
                }
            }
    }

    data class Params(val film: FilmDetail, val cinema: Cinema, val user: User)
}
