package be.hogent.faith.service.usecases.user

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateUserUseCase(
    private val userRepository: IUserRepository,
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack>,
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Backpack>,
    observer: Scheduler
) : SingleUseCase<User, CreateUserUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<User> {
        val user = User(params.username, params.avatarName, params.uuid)

        val createUser = userRepository
            .insert(user)
        val createBackpack = backpackEncryptionService.createContainer()
            .flatMapCompletable(backpackRepository::saveEncryptedContainer)
        val createCinema = cinemaEncryptionService.createContainer()
            .flatMapCompletable(cinemaRepository::saveEncryptedContainer)

        return Completable.mergeArray(
            createUser,
            createCinema,
            createBackpack
        ).andThen(
            Single.just(user)
        )
    }

    data class Params(
        val username: String,
        val avatarName: String,
        val uuid: String
    )
}