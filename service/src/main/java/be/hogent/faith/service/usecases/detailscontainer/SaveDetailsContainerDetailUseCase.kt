package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveDetailsContainerDetailUseCase<T : DetailsContainer>(
    private val detailContainerRepository: IDetailContainerRepository<T>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<T>,
    private val storageRepository: IFileStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveDetailsContainerDetailUseCase.Params>(
    observeScheduler
) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return detailContainerRepository.getEncryptedContainer()
            .flatMap { container ->
                detailContainerEncryptionService.encrypt(
                    params.detail,
                    container
                )
            }
            .flatMap { encryptedDetail ->
                storageRepository.saveDetailFileWithContainer(
                    encryptedDetail,
                    params.detailsContainer
                )
            }
            .flatMapCompletable { savedEncryptedDetail ->
                detailContainerRepository.insertDetail(
                    savedEncryptedDetail,
                    params.user
                )
            }
            .andThen { addDetailToContainer(params) }
    }

    private fun addDetailToContainer(params: Params) {
        with(params) {
            detailsContainer.addDetail(detail)
        }
    }

    data class Params(val user: User, val detailsContainer: DetailsContainer, val detail: Detail)
}
