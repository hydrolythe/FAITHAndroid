package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateTextDetailUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<TextDetail, CreateTextDetailUseCase.SaveTextParams>(observeScheduler) {


    override fun buildUseCaseSingle(params: SaveTextParams): Single<TextDetail> {
            storageRepository.saveText(params.text, params.event)
                .doOnSuccess { storedFile ->
                    params.event.addNewTextDetail(storedFile)
                })
    }

    class SaveTextParams(
        val text: String,
        val existingDetail: TextDetail? = null
    )
}
