package be.hogent.faith.service.usecases.user

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.ContainerType
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class InitialiseUserUseCase(
    private val userRepository: IUserRepository,
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val treasureChestRepository: IDetailContainerRepository<TreasureChest>,
    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack>,
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Cinema>,
    private val treasureChestEncryptionService: IDetailContainerEncryptionService<TreasureChest>,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : CompletableUseCase<InitialiseUserUseCase.Params>(observer, subscriber) {

    override fun buildUseCaseObservable(params: Params): Completable {
        val createUser = userRepository
            .initialiseUser(params.user)
            .doOnComplete { Timber.i("Created user in repo") }
            .doOnError { Timber.e("Fout bij initialisatie avatar") }

        val createBackpack = backpackEncryptionService.createContainer(ContainerType.BACKPACK)
            .subscribeOn(subscriber)
            .doOnSuccess { Timber.i("Created container for backpack") }
            .doOnError { Timber.e("Fout bij aanmaken container backpack") }
            .flatMapCompletable(backpackRepository::saveEncryptedContainer)
            .doOnComplete { Timber.i("Saved container for backpack") }
            .doOnError { Timber.e("Fout bij opslaan container backpack") }

        val createCinema = cinemaEncryptionService.createContainer(ContainerType.CINEMA)
            .subscribeOn(subscriber)
            .doOnSuccess { Timber.i("Created container for cinema") }
            .doOnError {
                Timber.e("Fout bij aanmaken container cinema: ${it.localizedMessage}")
            }
            .flatMapCompletable(cinemaRepository::saveEncryptedContainer)
            .doOnComplete { Timber.i("Saved container for cinema") }
            .doOnError {
                Timber.e("Fout bij opslaan container cinema")
                it.printStackTrace()
            }

        val createTreasureChest =
            treasureChestEncryptionService.createContainer(ContainerType.TREASURECHEST)
                .subscribeOn(subscriber)
                .doOnSuccess { Timber.i("Created container for treasurechest") }
                .doOnError { Timber.e("Fout bij aanmaken container schatkist: ${it.localizedMessage}") }
                .flatMapCompletable(treasureChestRepository::saveEncryptedContainer)
                .doOnComplete { Timber.i("Saved container for treasurechest") }
                .doOnError {
                    Timber.e("Fout bij opslaan container schatkist")
                    it.printStackTrace()
                }

        return createUser
            .andThen(
                Completable.defer { createCinema }
            )
            .andThen(
                Completable.defer { createTreasureChest }
            )
            .andThen(
                Completable.defer { createBackpack }
            )
    }

    data class Params(
        val user: User
    )
}