package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageInterface
import be.hogent.faith.storage.localStorage.TemporaryStorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single

class LoadTextDetailUseCase(
    private val storageRepository: TemporaryStorageInterface,
    observeScheduler: Scheduler
) : SingleUseCase<String, LoadTextDetailUseCase.LoadTextParams>(observeScheduler) {

    override fun buildUseCaseSingle(params: LoadTextParams): Single<String> {
        return storageRepository.loadTextFromExistingDetail(params.textDetail)
    }

    class LoadTextParams(
        val textDetail: TextDetail
    )
}
