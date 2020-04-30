package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler

/**
 * Use to load the data for all details in the backpack
 */
class GetBackPackDataUseCase(
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Backpack>,
    observeScheduler: Scheduler
) : ObservableUseCase<List<Detail>, GetBackPackDataUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Observable<List<Detail>> {
        return backpackRepository.getEncryptedContainer()
            .flatMapObservable { container ->
                backpackRepository.getAll()
                    .toObservable()
                    .concatMapSingle { list ->
                        Observable.fromIterable(list)
                            .flatMapSingle {
                                detailContainerEncryptionService.decryptData(
                                    it,
                                    container
                                )
                            }
                            .toList()
                    }
            }
    }

    class Params
}