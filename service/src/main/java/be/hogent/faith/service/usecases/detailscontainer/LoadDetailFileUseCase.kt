package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber
import kotlin.math.log

class LoadDetailFileUseCase<Container : DetailsContainer>(
    private val storageRepo: IFileStorageRepository,
    private val containerRepository: IDetailContainerRepository<Container>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Container>,
    observer: Scheduler
) : CompletableUseCase<LoadDetailFileUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        if (storageRepo.setFileIfReady(params.detail, params.container)) {
            return Completable.complete()
        } else {
            return storageRepo.downloadFile(params.detail, params.container).doOnError{
                Timber.e("Could not download file")
            }
                .andThen(containerRepository.getEncryptedContainer()).doOnError{
                    Timber.e("Could not get container")
                }
                .flatMapCompletable { container ->
                    detailContainerEncryptionService.decryptFile(params.detail, container).doOnError{
                        Timber.e("Could not decrypt file")
                    }
                }
        }
    }

    class Params(
        val detail: Detail,
        val container: DetailsContainer
    )
}
