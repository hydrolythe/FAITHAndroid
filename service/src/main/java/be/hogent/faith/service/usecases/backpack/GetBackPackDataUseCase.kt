package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler

/**
 * Use to load the data for all details in the backpack
 */
class GetBackPackDataUseCase(
    private val backpackRepository: IDetailContainerRepository<Backpack>,
    private val detailContainerEncryptionService: IDetailContainerEncryptionService<Backpack>,
    observeScheduler: Scheduler
) : FlowableUseCase<List<Detail>, GetBackPackDataUseCase.Params>(observeScheduler) {
    override fun buildUseCaseObservable(params: Params): Flowable<List<Detail>> {
        // TODO: find something around this blockingGet
        val container = backpackRepository.getEncryptedContainer().blockingGet()
        return backpackRepository.getAll()
            .concatMapSingle { list ->
                Observable.fromIterable(list)
                    .flatMapSingle { detailContainerEncryptionService.decryptData(it, container) }
                    .toList()
            }
    }

    class Params
}