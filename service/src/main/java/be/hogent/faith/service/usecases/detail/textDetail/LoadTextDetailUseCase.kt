package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class LoadTextDetailUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observer: Scheduler
) : SingleUseCase<String, LoadTextDetailUseCase.LoadTextParams>(observer) {

    override fun buildUseCaseSingle(params: LoadTextParams): Single<String> {
        return tempStorageRepo.loadTextFromExistingDetail(params.textDetail)
    }

    class LoadTextParams(
        val textDetail: TextDetail
    )
}
