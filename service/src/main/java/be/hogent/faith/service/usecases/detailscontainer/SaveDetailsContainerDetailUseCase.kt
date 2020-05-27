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
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SaveDetailsContainerDetailUseCase<Container : DetailsContainer>(
    private val detailContainerRepository: IDetailContainerRepository<Container>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Container>,
    private val storageRepository: IFileStorageRepository,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : CompletableUseCase<SaveDetailsContainerDetailUseCase.Params>(observer, subscriber) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return detailContainerRepository.getEncryptedContainer()
            .subscribeOn(subscriber)
            .doOnSuccess { Timber.i("Got encrypted container for ${params.detailsContainer.javaClass}") }
            .doOnError {
                Timber.e("Error while fetching ${params.detailsContainer.javaClass}: ${it.localizedMessage}")
            }
            .flatMap { container ->
                detailContainerEncryptionService.encrypt(params.detail, container)
                    .subscribeOn(subscriber)
                    .doOnSuccess { Timber.i("Encrypted detail ${params.detail.uuid} in ${params.detailsContainer.javaClass}") }
                    .doOnError {
                        Timber.e("Error while encrypting detail $params.detail.uuid} in ${params.detailsContainer.javaClass}: ${it.localizedMessage}")
                    }
            }
            .flatMap { encryptedDetail ->
                storageRepository.saveDetailFileWithContainer(
                    encryptedDetail,
                    params.detailsContainer
                )
                    .subscribeOn(subscriber)
                    .doOnSuccess { Timber.i("Stored detail file${params.detail.uuid} in ${params.detailsContainer.javaClass}") }
                    .doOnError {
                        Timber.e("Error while storing detail file $params.detail.uuid} in ${params.detailsContainer.javaClass}: ${it.localizedMessage}")
                        it.printStackTrace()
                    }
            }
            .flatMapCompletable { savedEncryptedDetail ->
                detailContainerRepository.insertDetail(savedEncryptedDetail, params.user)
                    .subscribeOn(subscriber)
                    .doOnComplete { Timber.i("Stored detail  data ${params.detail.uuid} in ${params.detailsContainer.javaClass}") }
                    .doOnError {
                        Timber.e("Error while storing detail data $params.detail.uuid} in ${params.detailsContainer.javaClass}: ${it.localizedMessage}")
                        it.printStackTrace()
                    }
            }.andThen(
                Completable.defer { Completable.fromAction { addDetailToContainer(params) } }
                    .doOnComplete { Timber.i("Detail ${params.detail.uuid} added to ${params.detailsContainer.javaClass.simpleName}") }
            )
    }

    private fun addDetailToContainer(params: Params) {
        with(params) { detailsContainer.addDetail(detail) }
    }

    data class Params(val user: User, val detailsContainer: DetailsContainer, val detail: Detail)
}
