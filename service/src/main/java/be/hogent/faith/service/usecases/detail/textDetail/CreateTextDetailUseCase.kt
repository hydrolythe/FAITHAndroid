package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageInterface
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateTextDetailUseCase(
    private val storageRepository: TemporaryStorageInterface,
    observeScheduler: Scheduler
) : SingleUseCase<TextDetail, CreateTextDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<TextDetail> {
        return storageRepository.storeTextTemporarily(params.text)
            .map { saveFile -> TextDetail(saveFile) }
    }

    class Params(
        val text: String
    )
}
