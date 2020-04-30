package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.Cinema
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
class GetCinemaDataUseCase(
    private val cinemaRepository: IDetailContainerRepository<Cinema>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Cinema>,
    observeScheduler: Scheduler,
    private val subscribeScheduler: Scheduler
) : ObservableUseCase<List<Detail>, GetCinemaDataUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Observable<List<Detail>> {
        return cinemaRepository.getEncryptedContainer()
            .subscribeOn(subscribeScheduler)
            .doOnSuccess { Timber.i("Got encrypted cinema") }
            .doOnError {
                Timber.e("Error while fetching encrypted cinema: ${it.localizedMessage}")
                it.printStackTrace()
            }
            .flatMapObservable { container ->
                cinemaRepository.getAll()
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

    class Params(
        cinema: Cinema
    )
}