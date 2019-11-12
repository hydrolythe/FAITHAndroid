package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateTextDetailUseCase(
    private val storageRepository: StorageRepository,
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
