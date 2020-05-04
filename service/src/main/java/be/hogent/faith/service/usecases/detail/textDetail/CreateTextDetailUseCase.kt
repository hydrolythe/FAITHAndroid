package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateTextDetailUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<TextDetail, CreateTextDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<TextDetail> {
        return tempStorageRepo.storeText(params.text)
            .map { saveFile -> TextDetail(saveFile) }
    }

    class Params(
        val text: String
    )
}
