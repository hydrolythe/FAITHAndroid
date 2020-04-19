package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class LoadDetailFileUseCase<Container : DetailsContainer>(
    private val storageRepo: IFileStorageRepository,
    private val containerRepository: IDetailContainerRepository<Container>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Container>,
    observeScheduler: Scheduler
) : SingleUseCase<File, LoadDetailFileUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<File> {
        if (storageRepo.fileReadyToUse(params.detail, params.container)) {
            return Single.just(params.detail.file)
        } else {
            return Single.just(params.detail.file)
            // TODO:
//            return storageRepo.downloadFile(params.detail, params.container)
//                .andThen(containerRepository.getEncryptedContainer())
//                .map { container ->
//                    detailContainerEncryptionService.decrypt(
//                        params.detail, container
//                    )
//                }
        }
    }

    class Params(
        val detail: Detail,
        val container: DetailsContainer
    )
}
