package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import io.reactivex.Scheduler
import io.reactivex.Single

class LoadTextDetailUseCase(
    private val tempStorageRepo: ITemporaryStorage,
    private val storageRepo: IStorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<String, LoadTextDetailUseCase.LoadTextParams>(observeScheduler) {

    override fun buildUseCaseSingle(params: LoadTextParams): Single<String> {
        return tempStorageRepo.loadTextFromExistingDetail(params.textDetail)
            .onErrorResumeNext(storageRepo.getFile(params.textDetail).map { it.readText() })
    }

    class LoadTextParams(
        val textDetail: TextDetail
    )
}
