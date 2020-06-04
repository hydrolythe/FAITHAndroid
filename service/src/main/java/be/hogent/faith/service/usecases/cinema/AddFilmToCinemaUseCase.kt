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
import timber.log.Timber

class AddFilmToCinemaUseCase(
    private val containerEncryptionService: IDetailContainerEncryptionService<Cinema>,
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val fileStorageRepository: IFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<AddFilmToCinemaUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return cinemaRepository.getEncryptedContainer()
            .doOnSuccess { Timber.i("Got cinema encrypted container") }
            .subscribeOn(subscriber)
            .flatMap { encryptedCinema ->
                containerEncryptionService.encrypt(params.film, encryptedCinema)
                    .subscribeOn(subscriber)
                    .doOnSuccess { Timber.i("Encrypted new film") }
            }
            .flatMap { encryptedMovie ->
                fileStorageRepository.saveDetailFileWithContainer(encryptedMovie, params.cinema)
                    .subscribeOn(subscriber)
                    .doOnSuccess { Timber.i("Stored new film") }
            }
            .flatMapCompletable { encryptedMovie ->
                cinemaRepository.insertDetail(encryptedMovie, params.user)
                    .subscribeOn(subscriber)
                    .doOnComplete { Timber.i("Stored new film in db") }
            }.andThen(
                Completable
                    .fromAction { params.cinema.addFilm(params.film) }
                    .doOnComplete { Timber.i("Added film to cinema object") }
            )
    }

    data class Params(val film: FilmDetail, val cinema: Cinema, val user: User)
}
