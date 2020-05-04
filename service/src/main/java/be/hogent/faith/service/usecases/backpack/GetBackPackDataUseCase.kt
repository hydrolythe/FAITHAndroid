package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber

/**
 * Use to load the data for all details in the backpack
 */
class GetBackPackDataUseCase(
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Backpack>,
    observeScheduler: Scheduler,
    private val subscribeScheduler: Scheduler
) : ObservableUseCase<List<Detail>, GetBackPackDataUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Observable<List<Detail>> {
        return backpackRepository.getEncryptedContainer()
            .subscribeOn(subscribeScheduler)
            .doOnSuccess { Timber.i("Got encrypted backpack") }
            .doOnError {
                Timber.e("Error while fetching encrypted backpack: ${it.localizedMessage}")
                it.printStackTrace()
            }
            .flatMapObservable { container ->
                backpackRepository.getAll()
                    .subscribeOn(subscribeScheduler)
                    .doOnNext { Timber.i("Got encrypted backpack data") }
                    .doOnError {
                        Timber.e("Error while fetching encrypted backpack data: ${it.localizedMessage}")
                        it.printStackTrace()
                    }
                    .toObservable()
                    .concatMapSingle { list ->
                        Observable.fromIterable(list)
                            .flatMapSingle {
                                detailContainerEncryptionService.decryptData(it, container)
                                    .subscribeOn(subscribeScheduler)
                                    .doOnSuccess { Timber.i("Decrypted data for detail ${it.uuid} in backpack") }
                                    .doOnError {
                                        Timber.e("Error while decrypting detail: ${it.localizedMessage}")
                                        it.printStackTrace()
                                    }
                            }.toList()
                    }
            }
    }

    class Params
}