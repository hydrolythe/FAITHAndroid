package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class LoadDetailFileUseCase<Container : DetailsContainer>(
    private val storageRepo: IFileStorageRepository,
    private val containerRepository: IDetailContainerRepository<Container>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Container>,
    observeScheduler: Scheduler
) : CompletableUseCase<LoadDetailFileUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        if (storageRepo.setFileIfReady(params.detail, params.container)) {
            return Completable.complete()
        } else {
            return storageRepo.downloadFile(params.detail, params.container)
                .andThen(containerRepository.getEncryptedContainer())
                .flatMapCompletable { container ->
                    detailContainerEncryptionService.decryptFile(params.detail, container)
                }
        }
    }

    class Params(
        val detail: Detail,
        val container: DetailsContainer
    )
}
