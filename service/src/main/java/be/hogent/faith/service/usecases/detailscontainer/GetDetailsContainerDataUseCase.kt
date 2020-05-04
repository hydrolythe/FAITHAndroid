package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber

/**
 * Use to load the data for all details in the cinema
 */
class GetDetailsContainerDataUseCase<T : DetailsContainer>(
    private val detailsContainerRepository: IDetailContainerRepository<T>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<T>,
    observeScheduler: Scheduler,
    private val subscribeScheduler: Scheduler
) : ObservableUseCase<List<Detail>, GetDetailsContainerDataUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Observable<List<Detail>> {
        return detailsContainerRepository.getEncryptedContainer()
            .subscribeOn(subscribeScheduler)
            .doOnSuccess { Timber.i("Got encrypted cinema") }
            .doOnError {
                Timber.e("Error while fetching encrypted cinema: ${it.localizedMessage}")
                it.printStackTrace()
            }
            .flatMapObservable { container ->
                detailsContainerRepository.getAll()
                    .subscribeOn(subscribeScheduler)
                    .doOnNext { Timber.i("Got encrypted cinema data") }
                    .doOnError {
                        Timber.e("Error while fetching encrypted cinema data: ${it.localizedMessage}")
                        it.printStackTrace()
                    }
                    .toObservable()
                    .concatMapSingle { list ->
                        Observable.fromIterable(list)
                            .flatMapSingle {
                                detailContainerEncryptionService.decryptData(it, container)
                                    .subscribeOn(subscribeScheduler)
                                    .doOnSuccess { Timber.i("Decrypted data for detail ${it.uuid} in cinema") }
                                    .doOnError {
                                        Timber.e("Error while decrypting detail: ${it.localizedMessage}")
                                        it.printStackTrace()
                                    }
                            }.toList()
                    }
            }
    }

    class Params()
}