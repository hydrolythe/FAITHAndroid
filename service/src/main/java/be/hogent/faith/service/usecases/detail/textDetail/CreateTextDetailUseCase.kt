package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class CreateTextDetailUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observer: Scheduler
) : SingleUseCase<TextDetail, CreateTextDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<TextDetail> {
        return tempStorageRepo.storeText(params.text)
            .map { saveFile -> TextDetail(saveFile) }
    }

    class Params(
        val text: String
    )
}
