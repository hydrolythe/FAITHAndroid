package be.hogent.faith.service.usecases.user

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class InitialiseUserUseCase(
    private val userRepository: IUserRepository,
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack>,
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Backpack>,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : CompletableUseCase<InitialiseUserUseCase.Params>(observer, subscriber) {

    override fun buildUseCaseObservable(params: Params): Completable {
        val user = userRepository
            .initialiseUser(User(params.user.username, params.user.avatarName, params.user.uuid))
            .doOnComplete { Timber.i("Created user in repo") }
            .doOnError{Timber.e("Fout bij initialisatie avatar")}

        val createBackpack = backpackEncryptionService.createContainer()
            .subscribeOn(subscriber)
            .doOnSuccess { Timber.i("Created container for backpack") }
            .doOnError { Timber.e("Fout bij aanmaken container backpack") }
            .flatMapCompletable(backpackRepository::saveEncryptedContainer)
            .doOnComplete { Timber.i("Saved container for backpack") }
            .doOnError { Timber.e("Fout bij opslaan container backpack") }

        val createCinema = cinemaEncryptionService.createContainer()
            .subscribeOn(subscriber)
            .doOnSuccess { Timber.i("Created container for cinema") }
            .doOnError {
                Timber.e("Fout bij aanmaken container cinema: ${it.localizedMessage}")
                it.printStackTrace()
            }
            .flatMapCompletable(cinemaRepository::saveEncryptedContainer)
            .doOnComplete { Timber.i("Saved container for cinema") }
            .doOnError {
                Timber.e("Fout bij opslaan container cinema")
                it.printStackTrace()
            }

        return user
            .andThen(
                Completable.defer { createCinema }
            )
            .andThen(
                Completable.defer { createBackpack }
            )
    }

    data class Params(
        val user: User
    )
}